/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.dto.request.OAuthRequest;
import br.com.conductor.heimdall.core.dto.response.TokenImplicit;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.OAuthAuthorize;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.entity.ProviderParam;
import br.com.conductor.heimdall.core.entity.TokenOAuth;
import br.com.conductor.heimdall.core.enums.TypeOAuth;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.exception.UnauthorizedException;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.service.OAuthService;
import br.com.conductor.heimdall.core.util.JwtUtils;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import br.com.conductor.heimdall.middleware.enums.HttpStatus;
import br.com.conductor.heimdall.middleware.enums.HttpStatus.Series;
import br.com.conductor.heimdall.middleware.spec.Http;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.util.ConstantsOAuth.GRANT_TYPE_IMPLICIT;
import static br.com.conductor.heimdall.core.util.ConstantsOAuth.GRANT_TYPE_PASSWORD;
import static br.com.conductor.heimdall.core.util.ConstantsOAuth.GRANT_TYPE_REFRESH_TOKEN;

/**
 * Provides methods to validate request with OAuthInterceptor.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
@Slf4j
@Service
public class OAuthInterceptorService {

    /**
     * OAuth token default expiration time.
     */
    private static final int TIME_ACCESS_TOKEN = 60;

    /**
     * OAuth refresh token default expiration time.
     */
    private static final int TIME_REFRESH_TOKEN = 180;

    private static final String CLIENT_ID = "client_id";

    private static final String ACESS_TOKEN = "access_token";

    private static final String BEARER = "bearer";

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private AppRepository appRepository;

    private RequestContext context;

    /**
     * Runs the service for the OAuth
     *
     * @param typeOAuth        type of the OAuth
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @param providerId       {@link Provider} id
     */
    public void execute(TypeOAuth typeOAuth, String privateKey, int timeAccessToken, int timeRefreshToken, Long providerId) {

        try {
            executeInterceptor(typeOAuth, privateKey, timeAccessToken, timeRefreshToken, providerId);
        } catch( HeimdallException ex ){
            generateResponseWithError( ex.getMsgEnum().getMessage(), ex.getMsgEnum().getHttpCode());
        }
    }

    /**
     * Method that sends a Response
     *
     * @param message    Response message
     * @param httpStatus {@link HttpStatus} of the response
     */
    private void generateResponseWithError(String message, int httpStatus) {
        message = "{ \"error\" : \"" + message + "\" }";
        TraceContextHolder.getInstance().getActualTrace().trace(message);
        context.getResponse().setStatus(httpStatus);
        context.addZuulResponseHeader("Content-Type", "application/json");
        context.setResponseBody(message);
        context.setSendZuulResponse(false);
    }

    /**
     * Method to validate Interceptor OAuth and execute the operation correct, accord the type of the OAuth.
     *
     * @param typeOAuth        type of the OAuth
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @param providerId       {@link Provider} id
     */
    public void executeInterceptor(TypeOAuth typeOAuth, String privateKey, int timeAccessToken, int timeRefreshToken, Long providerId) {

        this.context = RequestContext.getCurrentContext();
        OAuthRequest oAuthRequest = recoverOAuthRequest();

        if (typeOAuth == TypeOAuth.VALIDATE) {
            runValidate(privateKey);
            return;
        }

        HeimdallException
            .checkThrow(Objects.isNull(oAuthRequest.getGrantType()) || oAuthRequest.getGrantType().isEmpty(), ExceptionMessage.GRANT_TYPE_NOT_FOUND);

        HttpServletRequest request = context.getRequest();

        timeAccessToken = (timeAccessToken <= 0) ? TIME_ACCESS_TOKEN : timeAccessToken;
        timeRefreshToken = (timeRefreshToken <= 0) ? TIME_REFRESH_TOKEN : timeRefreshToken;

        switch (typeOAuth) {
            case GENERATE:
                runGenerate(oAuthRequest, request, privateKey, timeAccessToken, timeRefreshToken);
                return;
            case AUTHORIZE:
                runAuthorize(oAuthRequest, request, providerId, privateKey, timeAccessToken, timeRefreshToken);
                return;
            default:
                ExceptionMessage.TYPE_OAUTH_NOT_FOUND.raise();
        }

    }

    /**
     * Method to run OAuth of the type Authorize.
     *
     * @param oAuthRequest {@link OAuthRequest}
     * @param request       request in context
     * @param providerId   {@link Provider} id
     * @param privateKey    privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     */

    private void runAuthorize(OAuthRequest oAuthRequest, HttpServletRequest request, Long providerId, String privateKey, int timeAccessToken, int timeRefreshToken) {

        HeimdallException.checkThrow(Objects.isNull(oAuthRequest.getClientId()) || oAuthRequest.getClientId().isEmpty(), ExceptionMessage.CLIENT_ID_NOT_FOUND);

        switch (oAuthRequest.getGrantType().toUpperCase()) {
            case GRANT_TYPE_PASSWORD:
                passwordFlow(oAuthService.getProvider(providerId), oAuthRequest, request, privateKey, timeAccessToken, timeRefreshToken);
                break;
            case GRANT_TYPE_IMPLICIT:
                implicitFlow(oAuthService.getProvider(providerId), oAuthRequest, request, privateKey, timeAccessToken);
                break;
            case GRANT_TYPE_REFRESH_TOKEN:
                refreshFlow(oAuthRequest, request, privateKey, timeAccessToken, timeRefreshToken);
                break;
            default:
                ExceptionMessage.WRONG_GRANT_TYPE_INFORMED.raise();
        }

    }

    /**
     * Method to run OAuth of the type Generate.
     *
     * @param oAuthRequest     {@link OAuthRequest}
     * @param request          request in context
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @throws HeimdallException Code not found, code already used, grant_type not found
     */

    private void runGenerate(OAuthRequest oAuthRequest, HttpServletRequest request, String privateKey, int timeAccessToken, int timeRefreshToken) throws HeimdallException {
        String claimsJson = getBodyRequest(request);
        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, oAuthRequest.getClientId(), privateKey, timeAccessToken, timeRefreshToken, claimsJson);
        String tokenOAuthJson;
        try {
            tokenOAuthJson = mapper().writeValueAsString(tokenOAuth);
            generateResponseWithSuccess(tokenOAuthJson);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new HeimdallException(ExceptionMessage.GLOBAL_JSON_INVALID_FORMAT);
        }
    }

    /**
     * Method to validate the token
     *
     * @param privateKey privateKey used in Token
     * @throws HeimdallException Token invalid, Authorization not found, Token expired
     */
    private void runValidate(String privateKey) {

        String authorization = context.getRequest().getHeader("Authorization");

        HeimdallException.checkThrow((Objects.isNull(authorization) || authorization.isEmpty() ||
                !authorization.matches("Bearer .+")), ExceptionMessage.AUTHORIZATION_NOT_FOUND);


        String token = authorization.replace("Bearer ", "");

        oAuthService.tokenIsValid(token, privateKey);

        OAuthAuthorize oAuthAuthorizeFromToken = oAuthService.getOAuthAuthorizeFromToken(token);

        if (oAuthAuthorizeFromToken.getGrantType().equals(GRANT_TYPE_IMPLICIT)) {
            generateTokenImplicit(privateKey, token, oAuthAuthorizeFromToken);
            oAuthService.delete(oAuthAuthorizeFromToken);
        }
    }


    /**
     * Generate new Token in validation of the token.
     *
     * @param privateKey              privateKey used in Token
     * @param token                   token from the header request
     * @param oAuthAuthorizeFromToken {@link OAuthAuthorize} recover from database
     * @throws HeimdallException Token expired
     */
    private void generateTokenImplicit(String privateKey, String token, OAuthAuthorize oAuthAuthorizeFromToken) {
        TokenImplicit tokenImplicit = oAuthService.generateTokenImplicitFromOtherToken(token, privateKey, oAuthAuthorizeFromToken.getExpirationTime());
        if (Objects.nonNull(tokenImplicit)) {
            oAuthService.saveToken(
                    oAuthAuthorizeFromToken.getClientId(),
                    tokenImplicit.getAccessToken(),
                    JwtUtils.recoverDateExpirationFromToken(tokenImplicit.getAccessToken(), privateKey),
                    GRANT_TYPE_IMPLICIT,
                    oAuthAuthorizeFromToken.getExpirationTime()
            );
            context.getResponse().addHeader(ACESS_TOKEN, tokenImplicit.getAccessToken());
        }
    }

    /**
     * Method that recover {@link OAuthRequest} from the request
     *
     * @return The {@link OAuthRequest}
     */
    private OAuthRequest recoverOAuthRequest() {
        HttpServletRequest request = context.getRequest();

        String body = getBodyRequest(request);
        OAuthRequest oAuthRequest;

        if (Objects.isNull(body) || body.isEmpty()) {
            oAuthRequest = new OAuthRequest();
        } else {
            HeimdallException.checkThrow(!isJson(body), ExceptionMessage.GLOBAL_JSON_INVALID_FORMAT);
            try {
                oAuthRequest = mapper().readValue(body, OAuthRequest.class);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                oAuthRequest = new OAuthRequest();
            }
        }

        if (Objects.isNull(oAuthRequest.getClientId()) || oAuthRequest.getClientId().isEmpty()) {
            oAuthRequest.setClientId(request.getHeader(CLIENT_ID));
        }

        if (Objects.isNull(oAuthRequest.getGrantType()) || oAuthRequest.getGrantType().isEmpty()) {
            oAuthRequest.setGrantType(request.getHeader("grant_type"));
        }

        if (Objects.isNull(oAuthRequest.getRefreshToken()) || oAuthRequest.getRefreshToken().isEmpty()) {
            oAuthRequest.setRefreshToken(request.getHeader("refresh_token"));
        }

        if (Objects.isNull(oAuthRequest.getResponseType()) || oAuthRequest.getResponseType().isEmpty()) {
            oAuthRequest.setResponseType(request.getHeader("response_type"));
        }

        return oAuthRequest;
    }

    /**
     * Method that adds all parameters needed to make the request with the provider
     *
     * @param uri The {@link UriComponentsBuilder} to set query params
     * @param providerParams List of the {@link ProviderParam}
     * @return The {@link Http} result
     */
    private HttpEntity createHttpEntity(UriComponentsBuilder uri, List<ProviderParam> providerParams) {

        HttpHeaders headers = new HttpHeaders();
        String body = "";

        Map<String, Object> paramsBody = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        for (ProviderParam param : providerParams) {

            if (Objects.isNull(param.getValue()) || param.getValue().isEmpty()) {
                param.setValue(context.getRequest().getHeader(param.getName()));
            }

            switch (param.getLocation().toUpperCase()) {
                case "HEADER":
                    headers.add(param.getName(), param.getValue());
                    break;
                case "BODY":
                    paramsBody.put(param.getName(), param.getValue());
                    break;
                default:
                    queryParams.put(param.getName(), Collections.singletonList(param.getValue()));
                    break;
            }
        }

        if (paramsBody.size() > 0) {
            try {
                body = StringEscapeUtils.unescapeJava(mapper().writeValueAsString(paramsBody));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
        }

        uri.queryParams(queryParams);
        return new HttpEntity<>(body, headers);
    }

    /**
     * Method that sends a Response with success
     *
     * @param message Success message
     */
    private void generateResponseWithSuccess(String message) {
        TraceContextHolder.getInstance().getActualTrace().trace(message);

        context.getResponse().setStatus(HttpStatus.OK.value());
        context.addZuulResponseHeader("Content-Type", "application/json");
        context.addZuulResponseHeader("Cache-Control", "no-store");
        context.addZuulResponseHeader("Pragma", "no-cache");
        context.setResponseBody(message);
        context.setSendZuulResponse(false);
    }


    /*
     * OAuth2.0 Password Flow
     */

    private void passwordFlow(Provider provider, OAuthRequest oAuthRequest, HttpServletRequest request, String privateKey, int timeAccessToken, int timeRefreshToken) {
        String body = getBodyRequest(request);
        validateClientId(request.getHeader(CLIENT_ID));
        validateInProvider(provider, request.getHeader(CLIENT_ID), request.getHeader(ACESS_TOKEN));

        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, oAuthRequest.getClientId(), privateKey, timeAccessToken, timeRefreshToken, body);
        if (Objects.nonNull(tokenOAuth)) {
            tokenOAuth.setToken_type(BEARER);
            try {
                generateResponseWithSuccess(mapper().writeValueAsString(tokenOAuth));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /*
     * OAuth2.0 Implicit Flow
     */
    private void implicitFlow(Provider provider, OAuthRequest oAuthRequest, HttpServletRequest request, String privateKey, int timeAccessToken) {
        String body = getBodyRequest(request);
        validateClientId(request.getHeader(CLIENT_ID));
        validateInProvider(provider, request.getHeader(CLIENT_ID), request.getHeader(ACESS_TOKEN));

        TokenImplicit tokenImplicit = oAuthService.generateTokenImplicit(oAuthRequest, privateKey, timeAccessToken, body);
        if (Objects.nonNull(tokenImplicit)) {
            tokenImplicit.setToken_type(BEARER);
            try {
                generateResponseWithSuccess(mapper().writeValueAsString(tokenImplicit));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /*
     * OAuth2.0 Refresh Flow
     */
    private void refreshFlow(OAuthRequest oAuthRequest, HttpServletRequest request, String privateKey, int timeAccessToken, int timeRefreshToken) {
        String claimsJson = getBodyRequest(request);
        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, oAuthRequest.getClientId(), privateKey, timeAccessToken, timeRefreshToken, claimsJson);
        if (Objects.nonNull(tokenOAuth)) {
            tokenOAuth.setToken_type(BEARER);
            try {
                generateResponseWithSuccess(mapper().writeValueAsString(tokenOAuth));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    private void validateClientId(String clientId) {
        final App appActive = appRepository.findAppActive(clientId);
        HeimdallException.checkThrow(Objects.isNull(appActive), ExceptionMessage.CLIENT_ID_NOT_FOUND);
    }


    private void validateInProvider(Provider provider, String clientId, String accessToken) {
        if (provider.isProviderDefault()) {
            final App appActive = appRepository.findAppActive(clientId);

            final List<AccessToken> accessTokens = appActive.getAccessTokens();
            HeimdallException.checkThrow(accessTokens.stream().noneMatch(ac -> ac.getCode().equals(accessToken)), ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);
        } else {

            RestTemplate template = new RestTemplate();
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(provider.getPath());

            try {
                ResponseEntity<String> entityResponse = template
                    .exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, createHttpEntity(uriComponentsBuilder, provider.getProviderParams()), String.class);

                HeimdallException.checkThrow((Series.valueOf(entityResponse.getStatusCodeValue()) != Series.SUCCESSFUL), ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new UnauthorizedException(ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);
            }
        }
    }

    private ObjectMapper mapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);

        return mapper;
    }

    public boolean isJson(String string) {

        try{
            mapper().readTree(string);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String getBodyRequest(HttpServletRequest request){
        String body = "";

        try {
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return body;
    }
}
