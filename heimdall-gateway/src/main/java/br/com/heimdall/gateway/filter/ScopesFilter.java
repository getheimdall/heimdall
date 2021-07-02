/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.heimdall.gateway.filter;

import br.com.heimdall.core.entity.App;
import br.com.heimdall.core.repository.AppRepository;
import br.com.heimdall.core.util.Constants;
import br.com.heimdall.core.trace.FilterDetail;
import br.com.heimdall.core.trace.TraceContextHolder;
import br.com.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Filter responsible for the access control to the Scopes
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Component
public class ScopesFilter extends ZuulFilter {

    @Autowired
    private AppRepository appRepository;

    private FilterDetail detail = new FilterDetail();

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 40;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() {
        long startTime = System.currentTimeMillis();
        try {
            process();
            detail.setStatus(Constants.SUCCESS);
        } catch (Throwable e) {
            detail.setStatus(Constants.FAILED);
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            detail.setTimeInMillisRun(duration);
            TraceContextHolder.getInstance().getActualTrace().addFilter(this.getClass().getSimpleName(), detail);
        }
        return null;
    }

    private void process() {
        final RequestContext context = RequestContext.getCurrentContext();

        final Long operation = (Long) context.get(ConstantsContext.OPERATION_ID);

        if (operation == null) return;

        final String client_id = context.getRequest().getHeader(ConstantsContext.CLIENT_ID);

        if (Objects.nonNull(client_id)) {
            App app = appRepository.findByClientId(client_id);
            if (Objects.isNull(app) || Objects.isNull(app.getPlans())) return;

            Set<Long> apis = app.getPlans().stream().map(plan -> plan.getApi().getId()).collect(Collectors.toSet());
            Long apiId = (Long) context.get(ConstantsContext.API_ID);
            if (!apis.contains(apiId)) return;

            final Set<Long> allowedOperations = new HashSet<>();

            app.getPlans()
                    .forEach(plan -> {
                        if (plan != null && plan.getApi().getId().equals(apiId))
                            plan.getScopes()
                                    .forEach(scope -> {
                                        if (scope != null)
                                            allowedOperations.addAll(scope.getOperationsIds());
                                    });
                    });

            // If the allowedOperations is empty it means that Scopes are not set
            if (allowedOperations.isEmpty()){
                return;
            }else if (!allowedOperations.contains(operation)) {
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                context.setResponseBody(HttpStatus.FORBIDDEN.getReasonPhrase());
                context.getResponse().setContentType(MediaType.TEXT_PLAIN_VALUE);
            }
        }
    }
}
