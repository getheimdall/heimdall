/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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
import br.com.conductor.heimdall.core.entity.*;
import br.com.conductor.heimdall.core.enums.TypeOAuth;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.exception.UnauthorizedException;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.service.OAuthService;
import br.com.conductor.heimdall.core.util.JwtUtils;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.middleware.enums.HttpStatus;
import br.com.conductor.heimdall.middleware.enums.HttpStatus.Series;
import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static br.com.conductor.heimdall.core.util.ConstantsOAuth.*;

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

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private AppRepository appRepository;

    private Helper helper;

    /**
     * Runs the service for the OAuth
     *
     * @param typeOAuth        type of the OAuth
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @param providerId       {@link Provider} id
     * @param helper           {@link Helper}
     */
    public void execute(TypeOAuth typeOAuth, String privateKey, int timeAccessToken, int timeRefreshToken, Long providerId, Helper helper) {

        try {
            executeInterceptor(typeOAuth, privateKey, timeAccessToken, timeRefreshToken, providerId, helper);
        } catch( HeimdallException ex ){
            generateResponse( ex.getMsgEnum().getMessage(), ex.getMsgEnum().getHttpCode());
        }
    }

    /**
     * Method that sends a Response
     *
     * @param message    Response message
     * @param httpStatus {@link HttpStatus} of the response
     */
    private void generateResponse(String message, int httpStatus) {
        message = "{ \"error\" : \"" + message + "\" }";
        TraceContextHolder.getInstance().getActualTrace().trace(message);
        this.helper.call().response().setStatus(httpStatus);
        this.helper.call().response().header().add("Content-Type", "application/json");
        this.helper.call().response().setBody(message);
    }

    /**
     * Method to validate Interceptor OAuth and execute the operation correct, accord the type of the OAuth.
     *
     * @param typeOAuth        type of the OAuth
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @param providerId       {@link Provider} id
     * @param helper           {@link Helper}
     */
    public void executeInterceptor(TypeOAuth typeOAuth, String privateKey, int timeAccessToken, int timeRefreshToken, Long providerId, Helper helper) {

        this.helper = helper;
        OAuthRequest oAuthRequest = recoverOAuthRequest();

        if (typeOAuth == TypeOAuth.VALIDATE) {
            runValidate(privateKey);
            return;
        }

        HeimdallException.checkThrow(Objeto.isBlank(oAuthRequest.getGrantType()), ExceptionMessage.GRANT_TYPE_NOT_FOUND);

        String body = helper.call().request().getBody();
        timeAccessToken = (timeAccessToken <= 0) ? TIME_ACCESS_TOKEN : timeAccessToken;
        timeRefreshToken = (timeRefreshToken <= 0) ? TIME_REFRESH_TOKEN : timeRefreshToken;

        String clientId = helper.call().request().header().get("client_id");
        String accessToken = helper.call().request().header().get("access_token");

        switch (typeOAuth) {
            case GENERATE:
                runGenerate(oAuthRequest, privateKey, timeAccessToken, timeRefreshToken, body);
                return;
            case AUTHORIZE:
                runAuthorize(oAuthRequest, clientId, providerId, privateKey, timeAccessToken, timeRefreshToken, body, accessToken);
                return;
            default:
                ExceptionMessage.TYPE_OAUTH_NOT_FOUND.raise();
        }

    }

    /**
     * Method to run OAuth of the type Authorize.
     *
     * @param oAuthRequest {@link OAuthRequest}
     * @param providerId   {@link Provider} id
     */

    private void runAuthorize(OAuthRequest oAuthRequest, String clientId, Long providerId, String privateKey, int timeAccessToken, int timeRefreshToken, String claimsJson, String accessToken) {

        HeimdallException.checkThrow(Objeto.isBlank(oAuthRequest.getClientId()), ExceptionMessage.CLIENT_ID_NOT_FOUND);

        switch (oAuthRequest.getGrantType().toUpperCase()) {
            case GRANT_TYPE_PASSWORD:
                passwordFlow(oAuthService.getProvider(providerId), oAuthRequest, clientId, privateKey, timeAccessToken, timeRefreshToken, claimsJson, accessToken);
                break;
            case GRANT_TYPE_IMPLICIT:
                implicitFlow(oAuthService.getProvider(providerId), oAuthRequest, clientId, privateKey, timeAccessToken, claimsJson, accessToken);
                break;
            case GRANT_TYPE_REFRESH_TOKEN:
                refreshFlow(oAuthRequest, privateKey, timeAccessToken, timeRefreshToken, claimsJson);
                break;
            default:
                ExceptionMessage.WRONG_GRANT_TYPE_INFORMED.raise();
        }

        // Not in use yet
//        switch (oAuthRequest.getResponseType().toLowerCase()) {
//            case CODE:
//                String codeAuthorize = oAuthService.generateAuthorize(oAuthRequest.getClientId());
//                generateResponseWithSuccess("{\""+CODE+"\": \"" + codeAuthorize + "\"}");
//                break;
//            case TOKEN:
//                TokenImplicit tokenImplicit = oAuthService.generateTokenImplicit(oAuthRequest, privateKey, timeAccessToken, claimsJson);
//                if (Objects.nonNull(tokenImplicit)) {
//                    generateResponseWithSuccess(helper.json().parse(tokenImplicit));
//                }
//                break;
//            default:
//                HeimdallException.checkThrow(true, ExceptionMessage.RESPONSE_TYPE_NOT_FOUND);
//        }
    }

    /**
     * Method to run OAuth of the type Generate.
     *
     * @param oAuthRequest     {@link OAuthRequest}
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @param claimsJson       Claims to payload in JSON
     * @throws HeimdallException Code not found, code already used, grant_type not found
     */
    private void runGenerate(OAuthRequest oAuthRequest, String privateKey, int timeAccessToken, int timeRefreshToken, String claimsJson) throws HeimdallException {
        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, oAuthRequest.getClientId(), privateKey, timeAccessToken, timeRefreshToken, claimsJson);
        String tokenOAuthJson = helper.json().parse(tokenOAuth);
        generateResponseWithSuccess(tokenOAuthJson);
    }

    /**
     * Method to validate the token
     *
     * @param privateKey privateKey used in Token
     * @throws HeimdallException Token invalid, Authorization not found, Token expired
     */
    private void runValidate(String privateKey) throws HeimdallException {

        String authorization = helper.call().request().header().get("Authorization");

        HeimdallException.checkThrow((Objeto.isBlank(authorization) ||
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
    private void generateTokenImplicit(String privateKey, String token, OAuthAuthorize oAuthAuthorizeFromToken) throws HeimdallException {
        TokenImplicit tokenImplicit = oAuthService.generateTokenImplicitFromOtherToken(token, privateKey, oAuthAuthorizeFromToken.getExpirationTime());
        if (Objects.nonNull(tokenImplicit)) {
            oAuthService.saveToken(
                    oAuthAuthorizeFromToken.getClientId(),
                    tokenImplicit.getAccessToken(),
                    JwtUtils.recoverDateExpirationFromToken(tokenImplicit.getAccessToken(), privateKey),
                    GRANT_TYPE_IMPLICIT,
                    oAuthAuthorizeFromToken.getExpirationTime()
            );
            helper.call().response().header().set("access_token", tokenImplicit.getAccessToken());
        }
    }

    /**
     * Method that recover {@link OAuthRequest} from the request
     *
     * @return The {@link OAuthRequest}
     */
    private OAuthRequest recoverOAuthRequest() {

        String body = helper.call().request().getBody();
        OAuthRequest oAuthRequest;

        if (Objeto.isBlank(body)) {
            oAuthRequest = new OAuthRequest();
        } else {
            HeimdallException.checkThrow(!helper.json().isJson(body), ExceptionMessage.GLOBAL_JSON_INVALID_FORMAT);

            oAuthRequest = helper.json().parse(body, OAuthRequest.class);
        }

        if (Objeto.isBlank(oAuthRequest.getClientId())) {
            oAuthRequest.setClientId(helper.call().request().header().get("client_id"));
        }

        if (Objeto.isBlank(oAuthRequest.getGrantType())) {
            oAuthRequest.setGrantType(helper.call().request().header().get("grant_type"));
        }

        if (Objeto.isBlank(oAuthRequest.getCode())) {
            oAuthRequest.setCode(helper.call().request().header().get("code"));
        }

        if (Objeto.isBlank(oAuthRequest.getRefreshToken())) {
            oAuthRequest.setRefreshToken(helper.call().request().header().get("refresh_token"));
        }

        if (Objeto.isBlank(oAuthRequest.getResponseType())) {
            oAuthRequest.setResponseType(helper.call().request().header().get("response_type"));
        }

        return oAuthRequest;
    }

    /**
     * Method that adds all parameters needed to make the request with the provider
     *
     * @param http           {@link Http}
     * @param providerParams List of the {@link ProviderParam}
     * @return The {@link Http} result
     */
    private Http addAllParamsToRequestProvider(Http http, List<ProviderParam> providerParams) {
        Map<String, Object> paramsBody = new HashMap<>();
        for (ProviderParam param : providerParams) {

            if (Objeto.isBlank(param.getValue())) {
                param.setValue(helper.call().request().header().get(param.getName()));
            }

            switch (param.getLocation().toUpperCase()) {
                case "HEADER":
                    http = http.header(param.getName(), param.getValue());
                    break;
                case "BODY":
                    paramsBody.put(param.getName(), param.getValue());
                    break;
                default:
                    http = http.queryParam(param.getName(), param.getValue());
                    break;
            }
        }

        if (paramsBody.size() > 0) {
            http = http.body(paramsBody);
        }

        return http;
    }

    /**
     * Method that sends a Response with success
     *
     * @param message Success message
     */
    private void generateResponseWithSuccess(String message) {
        TraceContextHolder.getInstance().getActualTrace().trace(message);
        helper.call().response().setStatus(HttpStatus.OK.value());
        helper.call().response().header().add("Content-Type", "application/json");
        helper.call().response().header().add("Cache-Control", "no-store");
        helper.call().response().header().add("Pragma", "no-cache");
        helper.call().response().setBody(message);
    }


    /*
     * OAuth2.0 Password Flow
     */

    private void passwordFlow(Provider provider, OAuthRequest oAuthRequest, String clientId, String privateKey, int timeAccessToken, int timeRefreshToken, String claimsJson, String accessToken) {
        validateClientId(clientId);
        validateInProvider(provider, clientId, accessToken);

        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, oAuthRequest.getClientId(), privateKey, timeAccessToken, timeRefreshToken, claimsJson);
        if (Objects.nonNull(tokenOAuth)) {
            tokenOAuth.setToken_type("bearer");
            generateResponseWithSuccess(helper.json().parse(tokenOAuth));
        }
    }

    /*
     * OAuth2.0 Implicit Flow
     */
    private void implicitFlow(Provider provider, OAuthRequest oAuthRequest, String clientId, String privateKey, int timeAccessToken, String claimsJson, String accessToken) {
        validateClientId(clientId);
        validateInProvider(provider, clientId, accessToken);

        TokenImplicit tokenImplicit = oAuthService.generateTokenImplicit(oAuthRequest, privateKey, timeAccessToken, claimsJson);
        if (Objects.nonNull(tokenImplicit)) {
            tokenImplicit.setToken_type("bearer");
            generateResponseWithSuccess(helper.json().parse(tokenImplicit));
        }
    }

    /*
     * OAuth2.0 Refresh Flow
     */
    private void refreshFlow(OAuthRequest oAuthRequest, String privateKey, int timeAccessToken, int timeRefreshToken, String claimsJson) {
        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, oAuthRequest.getClientId(), privateKey, timeAccessToken, timeRefreshToken, claimsJson);
        if (Objects.nonNull(tokenOAuth)) {
            tokenOAuth.setToken_type("bearer");
            generateResponseWithSuccess(helper.json().parse(tokenOAuth));
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

            Http http = helper.http().url(provider.getPath());
            http = addAllParamsToRequestProvider(http, provider.getProviderParams());

            try {

                final ApiResponse apiResponse = http.sendPost();
                HeimdallException.checkThrow(!(Series.valueOf(apiResponse.getStatus()) == Series.SUCCESSFUL), ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);

            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                throw new UnauthorizedException(ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);
            }
        }
    }
}
