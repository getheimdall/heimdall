package br.com.conductor.heimdall.gateway.service;

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

import static br.com.conductor.heimdall.core.util.ConstantsOAuth.*;
import br.com.conductor.heimdall.core.dto.request.OAuthRequest;
import br.com.conductor.heimdall.core.dto.response.TokenImplicit;
import br.com.conductor.heimdall.core.entity.OAuthAuthorize;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.entity.ProviderParam;
import br.com.conductor.heimdall.core.entity.TokenOAuth;
import br.com.conductor.heimdall.core.enums.TypeOAuth;
import br.com.conductor.heimdall.core.exception.*;
import br.com.conductor.heimdall.core.service.OAuthService;
import br.com.conductor.heimdall.core.util.JwtUtils;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.middleware.enums.HttpStatus;
import br.com.conductor.heimdall.middleware.enums.HttpStatus.Series;
import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.twsoftware.alfred.object.Objeto;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Provides methods to validate request with OAuthInterceptor.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Slf4j
@Service
public class OAuthInterceptorService {

    @Autowired
    private OAuthService oAuthService;

    private Helper helper;

    /**
     * Method to validate Interceptor OAuth and execute the operation correct, accord the type of the OAuth.
     *
     * @param typeOAuth        type of the OAuth
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     * @param providerId       {@link Provider} id
     * @param helper           {@link Helper}
     * @throws HeimdallException
     */
    public void executeInterceptor(String typeOAuth, String privateKey, int timeAccessToken, int timeRefreshToken, Long providerId, Helper helper) throws HeimdallException {
        this.helper = helper;
        OAuthRequest oAuthRequest = recoverOAuthRequest();

        if (typeOAuth.equals(TypeOAuth.GENERATE.getTypeOAuth())) {
            String body = helper.call().request().getBody();
            //set time to min 20 seconds when timeAccessToken is 0
            if (timeAccessToken == 0) {
                timeAccessToken = 20;
            }
            runGenerate(oAuthRequest, privateKey, timeAccessToken, timeRefreshToken, body);
        } else if (typeOAuth.equals(TypeOAuth.AUTHORIZE.getTypeOAuth())) {
            String body = helper.call().request().getBody();
            //set time to min 240 seconds when timeAccessToken is 0
            if (timeAccessToken == 0) {
                timeAccessToken = 240;
            }
            runAuthorize(oAuthRequest, providerId, privateKey, timeAccessToken, body);
        } else {
            runValidate(privateKey);
        }
    }

    /**
     * Method to run OAuth of the type Authorize.
     *
     * @param oAuthRequest {@link OAuthRequest}
     * @param providerId   {@link Provider} id
     * @throws HeimdallException Code not found, code already used, grant_type not found, response_type not found
     */
    private void runAuthorize(OAuthRequest oAuthRequest, Long providerId, String privateKey, int timeAccessToken, String claimsJson) throws HeimdallException{

        if (Objeto.isBlank(oAuthRequest.getClientId())) {
            throw new BadRequestException(ExceptionMessage.CLIENT_ID_NOT_FOUND);
        }

        Provider provider = oAuthService.getProvider(providerId);
        Http http = helper.http().url(provider.getPath());

        http = addAllParamsToRequestProvider(http, provider.getProviderParams());

        try {
            ApiResponse apiResponse = http.sendPost();
            if (!(Series.valueOf(apiResponse.getStatus()) == Series.SUCCESSFUL)) {
                throw new UnauthorizedException(ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new UnauthorizedException(ExceptionMessage.PROVIDER_USER_UNAUTHORIZED);
        }

        if (Objeto.isBlank(oAuthRequest.getResponseType())) {
            throw new BadRequestException(ExceptionMessage.RESPONSE_TYPE_NOT_FOUND);
        }

        switch (oAuthRequest.getResponseType().toLowerCase()) {
            case CODE:
                String codeAuthorize = oAuthService.generateAuthorize(oAuthRequest.getClientId());
                generateResponseWithSuccess("{\""+CODE+"\": \"" + codeAuthorize + "\"}");
                break;
            case TOKEN:
                TokenImplicit tokenImplicit = oAuthService.generateTokenImplicit(oAuthRequest, privateKey, timeAccessToken, claimsJson);
                if (Objects.nonNull(tokenImplicit)) {
                    generateResponseWithSuccess(helper.json().parse(tokenImplicit));
                }
                break;
            default:
                throw new BadRequestException(ExceptionMessage.RESPONSE_TYPE_NOT_FOUND);
        }
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
        TokenOAuth tokenOAuth = oAuthService.generateTokenOAuth(oAuthRequest, privateKey, timeAccessToken, timeRefreshToken, claimsJson);
        String tokenOAuthJson = helper.json().parse(tokenOAuth);
        generateResponseWithSuccess(tokenOAuthJson);
    }

    /**
     * Method to run OAuth of the type Validate.
     *
     * @param privateKey privateKey used in Token
     * @throws HeimdallException Token invalid, Authorization not found, Token expired
     */
    private void runValidate(String privateKey) throws HeimdallException {

        String authorization = helper.call().request().header().get("Authorization");
        //verify if Authorization exist
        if (Objeto.isBlank(authorization)) {
            //throw the Exception
            throw new UnauthorizedException(ExceptionMessage.AUTHORIZATION_NOT_FOUND);
        }

        //get token from Header
        String token = authorization.replace("Bearer ", "");
        //validate token
        oAuthService.tokenIsValid(token, privateKey);
        //obtain OAuthAuthorize from token
        OAuthAuthorize oAuthAuthorizeFromToken = oAuthService.getOAuthAuthorizeFromToken(token);
        //obtain request url from Context
        String requestURL = RequestContext.getCurrentContext().getRequest().getRequestURL().toString();
        if (StringUtils.endsWith(requestURL, "/")) {
            requestURL = StringUtils.removeEnd(requestURL.trim(), "/");
        }
        //validate if token contain a requestUrl in subject operations
        if (!oAuthService.tokenIsValidToResource(token, privateKey, requestURL)) {
            throw new ForbiddenException(ExceptionMessage.TOKEN_INVALID);
        }
        //verify if token is Implicit, case yes, generate new token
        if (oAuthAuthorizeFromToken.getGrantType().equals(GRANT_TYPE_IMPLICIT)) {
            generateTokenImplicit(privateKey, token, oAuthAuthorizeFromToken);
        }
        //delete token from database
        oAuthService.delete(oAuthAuthorizeFromToken);
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

            switch (param.getLocation()) {
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
        helper.call().response().setBody(message);
    }

}
