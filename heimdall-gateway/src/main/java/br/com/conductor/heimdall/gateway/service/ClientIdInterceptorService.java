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

import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.service.AppService;
import br.com.conductor.heimdall.core.service.DeveloperService;
import br.com.conductor.heimdall.core.service.PlanService;
import br.com.conductor.heimdall.core.util.ConstantsInterceptors;
import br.com.conductor.heimdall.core.util.DigestUtils;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static br.com.conductor.heimdall.gateway.util.ConstantsContext.CLIENT_ID;

/**
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class ClientIdInterceptorService {

    @Autowired
    private AppService appService;

    @Autowired
    private PlanService planService;

    @Autowired
    private DeveloperService developerService;

    /**
     * Validates if a client id is valid
     *
     * @param apiId Api id
     */
    public void validate(String apiId) {

        RequestContext context = RequestContext.getCurrentContext();

        String clientId = context.getZuulRequestHeaders().get(CLIENT_ID);

        if (clientId == null) clientId = context.getRequest().getHeader(CLIENT_ID);

        this.validateClientId(apiId, clientId);
    }

    /**
     * Method responsible for validating client_id in interceptor
     *
     * @param apiId    The apiId
     * @param clientId ClientId to be validated
     */
    private void validateClientId(String apiId, String clientId) {

        final String CLIENT_ID = "Client Id";

        if (clientId != null) {

            TraceContextHolder.getInstance().getActualTrace().setClientId(DigestUtils.digestMD5(clientId));
            App app = appService.findByClientId(clientId);
            if (app != null) {

                Plan plan = app.getPlans().stream()
                        .map(planId -> planService.find(planId))
                        .filter(p -> apiId.equals(p.getApiId())).findFirst().orElse(null);
                if (plan != null) {
                    TraceContextHolder.getInstance().getActualTrace().setApp(app.getName());
                    TraceContextHolder.getInstance().getActualTrace().setAppDeveloper(developerService.find(app.getDeveloperId()).getEmail());

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
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        ctx.setResponseBody(message);
    }

}
