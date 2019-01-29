/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import static br.com.conductor.heimdall.gateway.util.ConstantsContext.*;

import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.Location;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.Objects;
import java.util.Set;

/**
 * Provides the validation for each of the Interceptor Life Cycles.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class LifeCycleService {

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SecurityService securityService;

    public boolean should(InterceptorLifeCycle interceptorLifeCycle,
                          Set<String> pathsAllowed,
                          Set<String> pathsNotAllowed,
                          String inboundURL,
                          String method,
                          HttpServletRequest req,
                          Long referenceId) {


        if (referenceId == null) return false;

        RequestContext context = RequestContext.getCurrentContext();

        switch (interceptorLifeCycle) {
            case API:
                Long apiId = (Long) context.get(API_ID);
                return referenceId.equals(apiId);

            case PLAN:
                return validatePlan(pathsAllowed, pathsNotAllowed, req, referenceId);

            case RESOURCE:
                Long resourceId = (Long) context.get(RESOURCE_ID);
                return referenceId.equals(resourceId);

            case OPERATION:
                Long operationId = (Long) context.get(OPERATION_ID);
                return referenceId.equals(operationId);

            default:
                return false;
        }

    }

    private boolean validatePlan(Set<String> pathsAllowed, Set<String> pathsNotAllowed, HttpServletRequest req, Long referenceId) {

        final Plan activePlan = planRepository.findOne(referenceId);
        if (activePlan == null || !Status.ACTIVE.equals(activePlan.getStatus()))
            return false;

        if (listContainURI(req.getRequestURI(), pathsNotAllowed)) {
            return false;
        }

        if (req.getHeader(CLIENT_ID) != null) {
            final App app = appRepository.findByClientId(req.getHeader(CLIENT_ID));

            if (Objects.isNull(app)) return false;

            if (app.getPlans().stream()
                    .noneMatch(plan -> plan.getId().equals(referenceId))) return false;

            return listContainURI(req.getRequestURI(), pathsAllowed);
        }

        return false;
    }

    public boolean validateClientId(HttpServletRequest req,
                                    Long apiId,
                                    Location location,
                                    String name) {

        String clientId;
        if (Location.HEADER.equals(location))
            clientId = req.getHeader(name);
        else
            clientId = req.getParameter(name);

        RequestContext requestContext = RequestContext.getCurrentContext();

        final Long currentApiId = (Long) requestContext.get(API_ID);

        if (apiId.equals(currentApiId)) {
            securityService.validateClientId(requestContext, currentApiId, clientId);
            return true;
        }

        return false;
    }

	public boolean validateAccessToken(HttpServletRequest req, Long apiId, Location location, String name) {

        String clientId;
        if (Location.HEADER.equals(location))
            clientId = req.getHeader(CLIENT_ID);
        else
            clientId = req.getParameter(CLIENT_ID);

        String accessToken;
        if (Location.HEADER.equals(location))
            accessToken = req.getHeader(name);
        else
            accessToken = req.getParameter(name);

        RequestContext requestContext = RequestContext.getCurrentContext();

        final Long currentApiId = (Long) requestContext.get(API_ID);

        if (apiId.equals(currentApiId)) {
            securityService.validadeAccessToken(requestContext, currentApiId, clientId, accessToken);
            return true;
        }

        return false;
    }

    private boolean listContainURI(String uri, Set<String> paths) {

        if (paths != null && uri != null) {

            uri = StringUtils.removeEnd(uri.trim(), "/");

            for (String path : paths) {
                if (uri.contains(path))
                    return true;
            }
        }

        return false;
    }
}
