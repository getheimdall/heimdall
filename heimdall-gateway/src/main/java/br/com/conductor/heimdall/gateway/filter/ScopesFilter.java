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
package br.com.conductor.heimdall.gateway.filter;

import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.trace.FilterDetail;
import br.com.conductor.heimdall.gateway.trace.StackTraceImpl;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;

import static br.com.conductor.heimdall.gateway.util.ConstantsContext.CLIENT_ID;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.OPERATION_ID;
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
            TraceContextHolder.getInstance().getActualTrace().setStackTrace(new StackTraceImpl(e.getClass().getName(), e.getMessage(), ExceptionUtils.getStackTrace(e)));
        } finally {
            long endTime = System.currentTimeMillis();

            long duration = (endTime - startTime);

            detail.setName(this.getClass().getSimpleName());
            detail.setTimeInMillisRun(duration);
            TraceContextHolder.getInstance().getActualTrace().addFilter(detail);
        }
        return null;
    }

    private void process() {
        final RequestContext context = RequestContext.getCurrentContext();

        final String client_id = context.getRequest().getHeader(CLIENT_ID);

        if (client_id != null) {
            final Set<BigInteger> allowedOperations = appRepository.findAllOperationIdsFromScopesByClientId(client_id);

            final Long opFromCtx = (Long) context.get(OPERATION_ID);
            if (Objects.isNull(opFromCtx)) return;

            final BigInteger operation = new BigInteger(opFromCtx.toString());

            // If the allowedOperations is empty it means that Scopes are not set
            if (allowedOperations == null || allowedOperations.isEmpty()) return;

            if (!allowedOperations.contains(operation)) {
                context.setSendZuulResponse(false);
                context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                context.setResponseBody(HttpStatus.FORBIDDEN.getReasonPhrase());
                context.getResponse().setContentType(MediaType.TEXT_PLAIN_VALUE);
            }
        }
    }
}
