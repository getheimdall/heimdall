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

import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import br.com.conductor.heimdall.middleware.enums.HttpStatus;
import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides the methods used by the CORS Interceptors
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class CORSInterceptorService {

    public void executeCorsPreFilter(Map<String, String> cors) {
        RequestContext ctx = RequestContext.getCurrentContext();

        if (ctx.getRequest().getMethod().equals(HttpMethod.OPTIONS.name())) {
            addHeadersToResponseOptions(cors);
            ctx.setSendZuulResponse(false);
            ctx.getResponse().setStatus(HttpStatus.OK.value());
        } else {
            ctx.set(ConstantsContext.CORS_FILTER, cors);
        }
    }

    public void executeCorsPostFilter(Map<String, String> cors) {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
        List<String> headersFromResponse = zuulResponseHeaders.stream().map(Pair::first).collect(Collectors.toList());

        cors.entrySet()
                .stream()
                .filter(entry -> !headersFromResponse.contains(entry.getKey()))
                .forEach(entry -> response.setHeader(entry.getKey(), entry.getValue()));
    }

    private void addHeadersToResponseOptions(Map<String, String> cors) {
        RequestContext ctx = RequestContext.getCurrentContext();

        List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
        List<String> headersFromResponse = zuulResponseHeaders.stream().map(Pair::first).collect(Collectors.toList());

        cors.entrySet()
                .stream()
                .filter(entry -> !headersFromResponse.contains(entry.getKey()))
                .forEach(entry -> ctx.addZuulResponseHeader(entry.getKey(), entry.getValue()));
    }

}