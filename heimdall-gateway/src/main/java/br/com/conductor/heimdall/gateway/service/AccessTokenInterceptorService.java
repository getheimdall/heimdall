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

import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.Location;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.util.ConstantsInterceptors;
import br.com.conductor.heimdall.core.util.DigestUtils;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.util.Constants.INTERRUPT;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.ACCESS_TOKEN;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.CLIENT_ID;

/**
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class AccessTokenInterceptorService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    /**
     * Validates if a access token originated from {@link Location} is valid
     *
     * @param apiId    Api id
     * @param location {@link Location}
     */
    public void validate(Long apiId, Location location) {

        HttpServletRequest req = RequestContext.getCurrentContext().getRequest();

        String clientId;
        String accessToken;
        if (Location.HEADER.equals(location)) {
            clientId = req.getHeader(CLIENT_ID);
            accessToken = req.getHeader(ACCESS_TOKEN);
        } else {
            clientId = req.getParameter(CLIENT_ID);
            accessToken = req.getParameter(ACCESS_TOKEN);
        }

        this.validateAccessToken(apiId, clientId, accessToken);

    }

    /**
     * Method responsible for validating access_token in interceptor
     *
     * @param apiId       Api reference id
     * @param clientId    user client_id
     * @param accessToken access token
     */
    private void validateAccessToken(Long apiId, String clientId, String accessToken) {

        final String ACCESS_TOKEN = "Access Token";

        TraceContextHolder.getInstance().getActualTrace().setAccessToken(DigestUtils.digestMD5(accessToken));

        if ((accessToken != null && !accessToken.isEmpty()) && (clientId != null && !clientId.isEmpty())) {

            AccessToken token = accessTokenRepository.findAccessTokenActive(accessToken);

            if (token != null && token.getApp() != null) {

                List<Plan> plans = token.getApp().getPlans();
                Set<Long> collect = plans.parallelStream().map(plan -> plan.getApi().getId()).collect(Collectors.toSet());
                if (collect.contains(apiId)) {

                    String cId = token.getApp().getClientId();
                    if (clientId.equals(cId)) {

                        TraceContextHolder.getInstance().getActualTrace().setApp(token.getApp().getName());

                    } else {
                        buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
                    }
                } else {
                    buildResponse(ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                }
            } else {
                buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
            }
        } else {
            buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
        }
    }

    private void buildResponse(String message) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setSendZuulResponse(false);
        ctx.put(INTERRUPT, true);
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        ctx.setResponseBody(message);
    }
}
