
package br.com.conductor.heimdall.gateway.filter;

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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.util.RequestHelper;
import br.com.conductor.heimdall.gateway.zuul.route.HeimdallRoute;
import br.com.conductor.heimdall.gateway.zuul.route.ProxyRouteLocator;
import com.google.common.collect.Sets;
import com.netflix.zuul.context.RequestContext;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

@RunWith(MockitoJUnitRunner.class)
public class HeimdallDecorationFilterTest {

    private HeimdallDecorationFilter filter;

    @Mock
    private ProxyRouteLocator routeLocator;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private RequestHelper requestHelper;

    private ProxyRequestHelper proxyRequestHelper = new ProxyRequestHelper();

    private ZuulProperties properties = new ZuulProperties();

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private RequestContext ctx;

    @Before
    public void init() {

        this.filter = new HeimdallDecorationFilter(routeLocator, "/", properties, proxyRequestHelper, operationRepository, requestHelper);
        this.ctx = RequestContext.getCurrentContext();
        this.ctx.clear();
        this.ctx.setRequest(this.request);
        this.ctx.setResponse(this.response);
        TraceContextHolder.getInstance().init(true, "developer", this.request, false);
    }

    @Test
    public void matchUrl() {

        this.request.setRequestURI("/api/foo");
        this.request.setMethod(HttpMethod.GET.name());
        assertEquals("/api/foo", ctx.getRequest().getRequestURI());
    }

    @Test
    public void routeWithoutApiBasePath() {
        Api api = new Api();
        api.setId(10L);
        api.setBasePath("/v2");

        this.request.setRequestURI("/v2/api/foo/1");
        this.request.setMethod(HttpMethod.GET.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo/{id}", null, "my.dns.com.br", true, null, Sets.newConcurrentHashSet());
        routes.put("/v2/api/foo/{id}", route);

        Resource res = new Resource();
        res.setId(88L);
        res.setApi(api);
        api.setName("apiName");

        Environment environment = new Environment();
        environment.setInboundURL("http://localhost");
        environment.setOutboundURL("http://outbound:8080");
        environment.setVariables(new HashMap<>());
        List<Environment> environments = Lists.newArrayList();
        environments.add(environment);

        Operation opPost = new Operation(10L, HttpMethod.POST, "/api/foo", "POST description", res);
        opPost.getResource().getApi().setEnvironments(environments);
        Operation opGet = new Operation(10L, HttpMethod.GET, "/api/foo/{id}", "GET description", res);
        opGet.getResource().getApi().setEnvironments(environments);
        Operation opDelete = new Operation(10L, HttpMethod.DELETE, "/api/foo/{id}", "DELETE description", res);
        opDelete.getResource().getApi().setEnvironments(environments);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(operationRepository.findByEndPoint("/v2/api/foo/{id}")).thenReturn(Lists.newArrayList(opPost, opGet, opDelete));

        this.filter.run();

        assertEquals("/api/foo/1", this.ctx.get(REQUEST_URI_KEY));
        assertEquals(true, this.ctx.sendZuulResponse());
    }

    @Test
    public void matchRouteWithMultiEnvironments() {
        this.request.setRequestURI("/path/api/foo");
        this.request.setMethod(HttpMethod.GET.name());
        this.request.addHeader("host", "some-path.com");

        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/path/api/foo", null, "my.dns.com.br", true, null, Sets.newConcurrentHashSet());
        routes.put("/path/api/foo", route);

        Api api = new Api();
        api.setId(10L);
        api.setBasePath("/path");
        api.setName("apiName");

        Resource res = new Resource();
        res.setId(88L);
        res.setApi(api);

        Environment env1 = new Environment();
        env1.setInboundURL("https://some-path.com");
        env1.setOutboundURL("https://some-path.com");
        env1.setVariables(new HashMap<>());

        Environment env2 = new Environment();
        env2.setInboundURL("https://other-path.com");
        env2.setOutboundURL("https://other-path.com");
        env2.setVariables(new HashMap<>());

        List<Environment> environments = Lists.newArrayList();
        environments.add(env1);
        environments.add(env2);

        api.setEnvironments(environments);

        Operation opGet = new Operation(10L, HttpMethod.GET, "/api/foo", "GET description", res);
        Operation opDelete = new Operation(10L, HttpMethod.DELETE, "/api/foo", "DELETE description", res);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(operationRepository.findByEndPoint("/path/api/foo")).thenReturn(Lists.newArrayList(opGet, opDelete));

        this.filter.run();

        assertEquals("/api/foo", this.ctx.get(REQUEST_URI_KEY));
        assertTrue(this.ctx.sendZuulResponse());
    }

    @Test
    public void throwNotAllowedRoute() {
        Api api = new Api();
        api.setId(10L);
        api.setBasePath("/v2");

        this.request.setRequestURI("/v2/api/foo/1");
        this.request.setMethod(HttpMethod.DELETE.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo/{id}", null, "my.dns.com.br", true, null, Sets.newConcurrentHashSet());
        routes.put("/v2/api/foo/{id}", route);

        Resource res = new Resource();
        res.setId(88L);
        res.setApi(api);

        Operation opGet = new Operation(10L, HttpMethod.GET, "/api/foo/{id}", "GET description", res);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(operationRepository.findByEndPoint("/v2/api/foo/{id}")).thenReturn(Lists.newArrayList(opGet));


        this.filter.run();

        assertFalse(this.ctx.sendZuulResponse());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), this.ctx.getResponseStatusCode());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), this.ctx.getResponseBody());
    }

    @Test
    public void testCallMethodAll() {

        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Sets.newConcurrentHashSet());
        routes.put("/v2/api/foo", route);

        Api api = new Api();
        api.setId(10L);
        api.setBasePath("/v2");
        api.setName("apiName");

        Resource resource = new Resource();
        resource.setId(88L);
        resource.setApi(api);

        Operation opPost = new Operation(10L, HttpMethod.POST, "/api/foo", "post foo description", resource);
        Operation opDelete = new Operation(11L, HttpMethod.DELETE, "/api/foo", "delete foo description", resource);
        Operation opAll = new Operation(12L, HttpMethod.ALL, "/api/foo", "all foo description", resource);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(operationRepository.findByEndPoint("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opAll));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.GET.name(), this.ctx);
        assertNotNull(heimdallRoute);
        assertEquals("/api/foo", heimdallRoute.getRoute().getPath());
    }

    @Test
    public void testCallMethodDifferentTheAll() {

        this.request.setRequestURI("/v2/api/foo");
        this.request.setMethod(HttpMethod.GET.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Sets.newConcurrentHashSet());
        routes.put("/v2/api/foo", route);

        Api api = new Api();
        api.setId(10L);
        api.setBasePath("/v2");
        api.setName("apiName");

        Resource resource = new Resource();
        resource.setId(88L);
        resource.setApi(api);

        Operation opPost = new Operation(10L, HttpMethod.POST, "/api/foo", "post foo description", resource);
        Operation opDelete = new Operation(11L, HttpMethod.DELETE, "/api/foo", "delete foo description", resource);
        Operation opGet = new Operation(13L, HttpMethod.GET, "/api/foo", "all foo description", resource);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(operationRepository.findByEndPoint("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opGet));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.GET.name(), this.ctx);
        assertNotNull(heimdallRoute);
        assertEquals("/api/foo", heimdallRoute.getRoute().getPath());
    }
}
