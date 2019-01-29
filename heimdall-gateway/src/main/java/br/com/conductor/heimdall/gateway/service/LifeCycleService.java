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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.gateway.util.ConstantsContext.*;

/**
 * Provides the validation for each of the Interceptor Life Cycles.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class LifeCycleService {

    @Autowired
    private ApiRepository apiRepository;

    public boolean should(InterceptorLifeCycle interceptorLifeCycle,
                          Long referenceId,
                          Long apiId,
                          Set<Integer> ignoredResources,
                          Set<Integer> ignoredOperations) {

        if (referenceId == null) return false;
        RequestContext context = RequestContext.getCurrentContext();

        Long requestApiId = (Long) context.get(API_ID);
        if (!apiId.equals(requestApiId)) return false;

        Long resourceId = (Long) context.get(RESOURCE_ID);
        if (ignoredResources.contains(resourceId.intValue())) return false;

        Long operationId = (Long) context.get(OPERATION_ID);
        if (ignoredOperations.contains(operationId.intValue())) return false;

        switch (interceptorLifeCycle) {
            case API:
                return referenceId.equals(apiId);
            case PLAN:
                return validatePlan(apiId, referenceId);
            case RESOURCE:
                return referenceId.equals(resourceId);
            case OPERATION:
                return referenceId.equals(operationId);
            default:
                return false;
        }

    }

    private boolean validatePlan(Long apiId, Long referenceId) {

        Api api = apiRepository.findOne(apiId);

        if (api == null) return false;
        if (api.getPlans() == null || api.getPlans().isEmpty()) return false;

        Set<Long> plansIds = api.getPlans().stream().map(Plan::getId).collect(Collectors.toSet());

        return plansIds.contains(referenceId);
    }
}
