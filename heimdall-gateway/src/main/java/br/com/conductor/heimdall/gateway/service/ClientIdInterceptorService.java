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

import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.Location;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.util.ConstantsInterceptors;
import br.com.conductor.heimdall.core.util.DigestUtils;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static br.com.conductor.heimdall.core.util.Constants.INTERRUPT;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.CLIENT_ID;

/**
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class ClientIdInterceptorService {

    @Autowired
    private AppRepository appRepository;

    /**
     * Validates if a client id originated from {@link Location} is valid
     *
     * @param apiId    Api id
     * @param location {@link Location}
     */
    public void validate(Long apiId, Location location) {

        HttpServletRequest req = RequestContext.getCurrentContext().getRequest();

        String clientId;
        if (Location.HEADER.equals(location))
            clientId = req.getHeader(CLIENT_ID);
        else
            clientId = req.getParameter(CLIENT_ID);

        this.validateClientId(apiId, clientId);
    }

    /**
     * Method responsible for validating client_id in interceptor
     *
     * @param apiId    The apiId
     * @param clientId ClientId to be validated
     */
    private void validateClientId(Long apiId, String clientId) {

        final String CLIENT_ID = "Client Id";

        if (clientId != null) {

            TraceContextHolder.getInstance().getActualTrace().setClientId(DigestUtils.digestMD5(clientId));
            App app = appRepository.findByClientId(clientId);
            if (app != null) {

                Plan plan = app.getPlans().stream().filter(p -> apiId.equals(p.getApi().getId())).findFirst().orElse(null);
                if (plan != null) {
                    TraceContextHolder.getInstance().getActualTrace().setApp(app.getName());
                    TraceContextHolder.getInstance().getActualTrace().setAppDeveloper(app.getDeveloper().getEmail());

                } else {
                    buildResponse(ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                }
            } else {
                buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, CLIENT_ID));
            }
        } else {
            buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, CLIENT_ID));
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
