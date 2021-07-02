/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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
package br.com.heimdall.gateway.service;

import br.com.heimdall.core.entity.App;
import br.com.heimdall.core.entity.Plan;
import br.com.heimdall.core.enums.InterceptorLifeCycle;
import br.com.heimdall.core.repository.AppRepository;
import br.com.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Provides the validation for each of the Interceptor Life Cycles.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class LifeCycleService {

    @Autowired
    private AppRepository appRepository;

    public boolean should(InterceptorLifeCycle interceptorLifeCycle,
                          Long referenceId,
                          Long apiId,
                          Set<Integer> ignoredResources,
                          Set<Integer> ignoredOperations,
                          Boolean status) {

        if (!status) return false;

        if (referenceId == null) return false;
        RequestContext context = RequestContext.getCurrentContext();

        Long requestApiId = (Long) context.get(ConstantsContext.API_ID);
        if (!apiId.equals(requestApiId)) return false;

        Long resourceId = (Long) context.get(ConstantsContext.RESOURCE_ID);
        if (ignoredResources.contains(resourceId.intValue())) return false;

        Long operationId = (Long) context.get(ConstantsContext.OPERATION_ID);
        if (ignoredOperations.contains(operationId.intValue())) return false;

        switch (interceptorLifeCycle) {
            case API:
                return referenceId.equals(apiId);
            case PLAN:
                return validatePlan(context, referenceId);
            case RESOURCE:
                return referenceId.equals(resourceId);
            case OPERATION:
                return referenceId.equals(operationId);
            default:
                return false;
        }

    }

    private boolean validatePlan(RequestContext context, Long referenceId) {

        String clientId = context.getRequest().getHeader(ConstantsContext.CLIENT_ID);

        if (clientId == null) return false;

        App app = appRepository.findByClientId(clientId);

        if (app == null) return false;
        if (app.getPlans() == null || app.getPlans().isEmpty()) return false;

        Set<Long> plansIds = app.getPlans().stream().map(Plan::getId).collect(Collectors.toSet());

        return plansIds.contains(referenceId);
    }
}
