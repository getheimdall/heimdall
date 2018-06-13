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

import br.com.conductor.heimdall.core.dto.request.OAuthRequest;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.entity.ProviderParam;
import br.com.conductor.heimdall.core.entity.TokenOAuth;
import br.com.conductor.heimdall.core.enums.TypeOAuth;
import br.com.conductor.heimdall.core.service.OAuthService;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.middleware.enums.HttpStatus;
import br.com.conductor.heimdall.middleware.enums.HttpStatus.Series;
import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.twsoftware.alfred.object.Objeto;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to validate request with OAuthInterceptor.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
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
     */
    public void executeInterceptor(String typeOAuth, String privateKey, int timeAccessToken, int timeRefreshToken, Long providerId, Helper helper) {
        this.helper = helper;
        OAuthRequest oAuthRequest = recoverOAuthRequest();
        if (typeOAuth.equals(TypeOAuth.GENERATE.getTypeOAuth())) {
            runGenerate(oAuthRequest, privateKey, timeAccessToken, timeRefreshToken);
        } else if (typeOAuth.equals(TypeOAuth.AUTHORIZE.getTypeOAuth())) {
            runAuthorize(oAuthRequest, providerId);
        } else {
            runValidate(privateKey);
        }
    }

    /**
     * Method to run OAuth of the type Authorize.
     *
     * @param oAuthRequest {@link OAuthRequest}
     * @param providerId   {@link Provider} id
     */
    private void runAuthorize(OAuthRequest oAuthRequest, Long providerId) {

        Provider provider = oAuthService.getProvider(providerId);
        Http http = helper.http().url(provider.getPath());

        http = addAllParamsToRequestProvider(http, provider.getProviderParams());

        try {
            ApiResponse apiResponse = http.sendPost();
            if (Series.valueOf(apiResponse.getStatus()) == Series.SUCCESSFUL) {
                String codeAuthorize = oAuthService.generateAuthorize(oAuthRequest.getClientId());
                generateResponseWithSuccess("{\"code\": \"" + codeAuthorize + "\"}");
            } else {
                generateResponseWithError("User provider unauthorized");
            }
        } catch (Exception ex) {
            generateResponseWithError("User provider unauthorized or bad request");
        }
    }

    /**
     * Method to run OAuth of the type Generate.
     *
     * @param oAuthRequest     {@link OAuthRequest}
     * @param privateKey       privateKey used in Token
     * @param timeAccessToken  time to expire accessToken
     * @param timeRefreshToken time to expire refreshToken
     */
    private void runGenerate(OAuthRequest oAuthRequest, String privateKey, int timeAccessToken, int timeRefreshToken) {
        try {
            TokenOAuth tokenOAuth = oAuthService.generateToken(oAuthRequest, privateKey, timeAccessToken, timeRefreshToken);
            String tokenOAuthJson = helper.json().parse(tokenOAuth);
            generateResponseWithSuccess(tokenOAuthJson);
        } catch (Exception e) {
            generateResponseWithError(e.getMessage());
        }
    }

    /**
     * Method to run OAuth of the type Validate.
     *
     * @param privateKey privateKey used in Token
     */
    private void runValidate(String privateKey) {
        String authorization = helper.call().request().header().get("Authorization");
        if (Objeto.isBlank(authorization)) {
            generateResponseWithError("Authorization not defined in header.");
        }

        String token = authorization.replace("Bearer ", "");
        if (oAuthService.tokenExpired(token, privateKey)) {
            generateResponseWithError("Token expired");
        } else {
            String requestURL = RequestContext.getCurrentContext().getRequest().getRequestURL().toString();
            if (StringUtils.endsWith(requestURL, "/")) {
                requestURL = StringUtils.removeEnd(requestURL.trim(), "/");
            }

            if (!oAuthService.tokenIsValidToResource(token, privateKey, requestURL)) {
                generateResponseWithError("Token not valid to this request");
            }
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
     * Method that sends a Response with error
     *
     * @param message Error message
     */
    private void generateResponseWithError(String message) {
        generateResponse("{ \"error\" : \"" + message + "\" }", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Method that sends a Response with success
     *
     * @param message Success message
     */
    private void generateResponseWithSuccess(String message) {
        generateResponse(message, HttpStatus.OK);
    }

    /**
     * Method that sends a Response
     *
     * @param message    Response message
     * @param httpStatus {@link HttpStatus} of the response
     */
    private void generateResponse(String message, HttpStatus httpStatus) {
        TraceContextHolder.getInstance().getActualTrace().trace(message);
        helper.call().response().setStatus(httpStatus.value());
        helper.call().response().header().add("Content-Type", "application/json");
        helper.call().response().setBody(message);
    }
}
