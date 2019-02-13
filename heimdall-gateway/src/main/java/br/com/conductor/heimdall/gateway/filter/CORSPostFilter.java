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

import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.service.CORSInterceptorService;
import br.com.conductor.heimdall.gateway.trace.FilterDetail;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * Filter responsible to update the CORS headers.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Component
@Slf4j
public class CORSPostFilter extends ZuulFilter {

    private static final int SEND_CORS_RESPONSE_FILTER_ORDER = 999;

    @Autowired
    private CORSInterceptorService corsInterceptorService;

    private FilterDetail detail = new FilterDetail();

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_CORS_RESPONSE_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        return Objects.nonNull(ctx.get(ConstantsContext.CORS_FILTER));
    }

    @Override
    public Object run() {
        long startTime = System.currentTimeMillis();

        try {
            process();
            detail.setStatus(Constants.SUCCESS);
        } catch (Exception e) {
            detail.setStatus(Constants.FAILED);
            log.error("Error during CORSPostFilter", e);
        } finally {

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);

            detail.setTimeInMillisRun(duration);
            detail.setName(this.getClass().getSimpleName());
            TraceContextHolder.getInstance().getActualTrace().addFilter(detail);
        }

        return null;
    }

    private void process() {
        RequestContext requestContext = RequestContext.getCurrentContext();

        Map<String, String> cors = (Map<String, String>) requestContext.get(ConstantsContext.CORS_FILTER);
        corsInterceptorService.executeCorsPostFilter(cors);
    }
}
