package br.com.conductor.heimdall.gateway.service;

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

import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(MockitoJUnitRunner.class)
public class CorsServiceTest {

    @InjectMocks
    private CORSInterceptorService corsInterceptorService;

    private RequestContext ctx;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    private Map<String, String> cors = new HashMap<>();

    @Before
    public void setUp() {

        this.ctx = RequestContext.getCurrentContext();
        this.ctx.clear();

        this.cors.put("Access-Control-Allow-Origin", "http://my.dns.com");
        this.cors.put("Access-Control-Allow-Credentials", "true");
        this.cors.put("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, PATCH, OPTIONS");
        this.cors.put("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, Pier-Server");
        this.cors.put("Access-Control-Max-Age", "1800");

        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        createRequestDefault(request);
    }

    @Test
    public void executeCORSPreFilterWithMethodOption() {

        request.setMethod(HttpMethod.OPTIONS.name());

        this.ctx.setRequest(request);
        this.ctx.setResponse(response);

        this.corsInterceptorService.executeCorsPreFilter(cors);

        List<Pair<String, String>> zuulResponseHeaders = this.ctx.getZuulResponseHeaders();

        AtomicInteger count = new AtomicInteger();

        zuulResponseHeaders.forEach(pair-> {
            String value = cors.get(pair.first());
            if (value != null) {
                if (value.equals(pair.second())) {
                    count.getAndIncrement();
                }
            }
        });

        Assert.assertEquals(count.get(), cors.size());
    }

    @Test
    public void executeCORSPreFilterWithoutMethodOption() {

        request.setMethod(HttpMethod.GET.name());

        this.ctx.setRequest(request);
        this.ctx.setResponse(response);

        this.corsInterceptorService.executeCorsPreFilter(cors);

        Assert.assertNotNull(this.ctx.get(ConstantsContext.CORS_FILTER));
    }

    @Test
    public void executeCORSPostFilter() {

        request.setMethod(HttpMethod.POST.name());

        this.ctx.setRequest(request);
        this.ctx.setResponse(response);

        this.corsInterceptorService.executeCorsPostFilter(cors);

        HttpServletResponse response = ctx.getResponse();

        AtomicInteger count = new AtomicInteger();

        cors.forEach((key, value) -> {
            String header = response.getHeader(key);
            if (header != null) {
                if (header.equals(value)) {
                    count.getAndIncrement();
                }
            }
        });

        Assert.assertEquals(count.get(), cors.size());
    }

    private void createRequestDefault(MockHttpServletRequest request) {
        String uri = "http://simpleUri.com";
        request.addHeader("connection", "Keep-alive");
        request.addHeader("host", "http://another-host.com");
        request.addHeader("Content-Type", "application/json;utf-8");
        request.setRequestURI(uri);
    }
}
