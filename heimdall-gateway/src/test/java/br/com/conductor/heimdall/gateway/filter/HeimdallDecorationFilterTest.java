
package br.com.conductor.heimdall.gateway.filter;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.router.Credential;
import br.com.conductor.heimdall.gateway.router.CredentialRepository;
import br.com.conductor.heimdall.gateway.router.EnvironmentInfo;
import br.com.conductor.heimdall.gateway.router.EnvironmentInfoRepository;
import br.com.conductor.heimdall.gateway.util.RequestHelper;
import br.com.conductor.heimdall.gateway.zuul.route.HeimdallRoute;
import br.com.conductor.heimdall.gateway.zuul.route.ProxyRouteLocator;
import com.netflix.zuul.context.RequestContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

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

import com.netflix.zuul.context.RequestContext;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.gateway.router.Credential;
import br.com.conductor.heimdall.gateway.router.CredentialRepository;
import br.com.conductor.heimdall.gateway.router.EnvironmentInfo;
import br.com.conductor.heimdall.gateway.router.EnvironmentInfoRepository;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.util.RequestHelper;
import br.com.conductor.heimdall.gateway.zuul.route.HeimdallRoute;
import br.com.conductor.heimdall.gateway.zuul.route.ProxyRouteLocator;

@RunWith(MockitoJUnitRunner.class)
public class HeimdallDecorationFilterTest {

    private HeimdallDecorationFilter filter;

    @Mock
    private ProxyRouteLocator routeLocator;

    @Mock
    private EnvironmentInfoRepository environmentInfoRepository;

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private RequestHelper requestHelper;

    private ProxyRequestHelper proxyRequestHelper = new ProxyRequestHelper();

    private ZuulProperties properties = new ZuulProperties();

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private RequestContext ctx;

    @Before
    public void init() {

        this.filter = new HeimdallDecorationFilter(routeLocator, "/", properties, proxyRequestHelper, requestHelper, credentialRepository, environmentInfoRepository);
        this.ctx = RequestContext.getCurrentContext();
        this.ctx.clear();
        this.ctx.setRequest(this.request);
        this.ctx.setResponse(this.response);
        TraceContextHolder.getInstance().init(true, "developer", this.request, false, false, "", true);
    }

    @Test
    public void matchUrl() {

        this.request.setRequestURI("/api/foo");
        this.request.setMethod(HttpMethod.GET.name());
        assertEquals("/api/foo", ctx.getRequest().getRequestURI());
    }

    @Test
    public void routeWithoutApiBasePath() {

        this.request.setRequestURI("/v2/api/foo/1");
        this.request.setMethod(HttpMethod.GET.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo/{id}", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo/{id}", route);

        EnvironmentInfo environmentInfo = new EnvironmentInfo();
        environmentInfo.setId(1L);
        environmentInfo.setOutboundURL("http://outbound:8080");
        environmentInfo.setVariables(new HashMap<>());

        Credential opPost = new Credential(HttpMethod.POST.name(), "/api/foo", "/v2", "apiName", 10L, 88L, 10L, false);
        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo/{id}", "/v2", "apiName", 10L, 88L, 10L, false);
        Credential opDelete = new Credential(HttpMethod.DELETE.name(), "/api/foo/{id}", "/v2", "apiName", 10L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo/{id}")).thenReturn(Lists.newArrayList(opPost, opGet, opDelete));
        Mockito.when(environmentInfoRepository.findByApiIdAndEnvironmentInboundURL(10L, "http://localhost/v2/api/foo/1")).thenReturn(environmentInfo);

        this.filter.run();

        assertEquals("/api/foo/1", this.ctx.get(REQUEST_URI_KEY));
        assertTrue(this.ctx.sendZuulResponse());
    }

    @Test
    public void matchRouteWithMultiEnvironments() {
        this.request.setRequestURI("/path/api/foo");
        this.request.setMethod(HttpMethod.GET.name());
        this.request.addHeader("host", "some-path.com");

        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/path/api/foo", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/path/api/foo", route);

        EnvironmentInfo environmentInfo = new EnvironmentInfo();
        environmentInfo.setId(1L);
        environmentInfo.setOutboundURL("https://some-path.com");
        environmentInfo.setVariables(new HashMap<>());

        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo", "/path", "apiName", 10L, 88L, 10L, false);
        Credential opDelete = new Credential(HttpMethod.DELETE.name(), "/api/foo", "/path", "apiName", 10L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/path/api/foo")).thenReturn(Lists.newArrayList(opGet, opDelete));
        Mockito.when(environmentInfoRepository.findByApiIdAndEnvironmentInboundURL(10L, "some-path.com")).thenReturn(environmentInfo);

        this.filter.run();

        assertEquals("/api/foo", this.ctx.get(REQUEST_URI_KEY));
        assertTrue(this.ctx.sendZuulResponse());
    }

    @Test
    public void throwNotAllowedRoute() {
        this.request.setRequestURI("/v2/api/foo/1");
        this.request.setMethod(HttpMethod.DELETE.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo/{id}", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo/{id}", route);

        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo/{id}", "/path", "apiName", 10L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo/{id}")).thenReturn(Lists.newArrayList(opGet));

        this.filter.run();

        assertFalse(this.ctx.sendZuulResponse());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), this.ctx.getResponseStatusCode());
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), this.ctx.getResponseBody());
    }

    @Test
    public void testCallMethodAll() {

        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo", route);

        Credential opPost = new Credential(HttpMethod.POST.name(), "/api/foo/{id}", "/v2", "apiName", 10L, 88L, 10L, false);
        Credential opDelete = new Credential(HttpMethod.DELETE.name(), "/api/foo/{id}", "/v2", "apiName", 11L, 88L, 10L, false);
        Credential opAll = new Credential(HttpMethod.ALL.name(), "/api/foo/{id}", "/v2", "apiName", 12L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opAll));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.GET.name(), this.ctx);
        assertNotNull(heimdallRoute);
        assertEquals("/api/foo", heimdallRoute.getRoute().getPath());
    }

    @Test
    public void testCallMethodDifferentTheAll() {

        this.request.setRequestURI("/v2/api/foo");
        this.request.setMethod(HttpMethod.GET.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo", route);

        Credential opPost = new Credential(HttpMethod.POST.name(), "/api/foo", "/v2", "apiName", 10L, 88L, 10L, false);
        Credential opDelete = new Credential(HttpMethod.DELETE.name(), "/api/foo", "/v2", "apiName", 11L, 88L, 10L, false);
        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo", "/v2", "apiName", 11L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opGet));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.GET.name(), this.ctx);
        assertNotNull(heimdallRoute);
        assertEquals("/api/foo", heimdallRoute.getRoute().getPath());
    }

    @Test
    public void testCallMethodIsOptionWithCors() {

        this.request.setRequestURI("/v2/api/foo");
        this.request.setMethod(HttpMethod.OPTIONS.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo", route);

        Credential opPost = new Credential(HttpMethod.POST.name(), "/api/foo", "/v2", "apiName", 11L, 88L, 10L, true);
        Credential opDelete = new Credential(HttpMethod.DELETE.name(), "/api/foo", "/v2", "apiName", 12L, 88L, 10L, true);
        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo", "/v2", "apiName", 13L, 88L, 10L, true);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opGet));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.OPTIONS.name(), this.ctx);
        assertNotNull(heimdallRoute);
        assertEquals("/api/foo", heimdallRoute.getRoute().getPath());
    }

    @Test
    public void testCallMethodIsOptionWithoutCors() {
        this.request.setRequestURI("/v2/api/foo");
        this.request.setMethod(HttpMethod.OPTIONS.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo", route);


        Credential opPost = new Credential(HttpMethod.POST.name(), "/api/foo", "/v2", "apiName", 10L, 88L, 10L, false);
        Credential opDelete = new Credential(HttpMethod.DELETE.name(), "/api/foo", "/v2", "apiName", 12L, 88L, 10L, false);
        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo", "/v2", "apiName", 13L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opGet));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.OPTIONS.name(), this.ctx);
        assertTrue(heimdallRoute.isMethodNotAllowed());
    }

    @Test
    public void testCallMethodInOperationsWithoutCors() {

        this.request.setRequestURI("/v2/api/foo");
        this.request.setMethod(HttpMethod.OPTIONS.name());
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        ZuulRoute route = new ZuulRoute("idFoo", "/v2/api/foo", null, "my.dns.com.br", true, null, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        routes.put("/v2/api/foo", route);

        Credential opPost = new Credential(HttpMethod.POST.name(), "/api/foo", "/v2", "apiName", 10L, 88L, 10L, false);
        Credential opDelete = new Credential(HttpMethod.OPTIONS.name(), "/api/foo", "/v2", "apiName", 11L, 88L, 10L, false);
        Credential opGet = new Credential(HttpMethod.GET.name(), "/api/foo", "/v2", "apiName", 12L, 88L, 10L, false);

        Mockito.when(routeLocator.getAtomicRoutes()).thenReturn(new AtomicReference<>(routes));
        Mockito.when(credentialRepository.findByPattern("/v2/api/foo")).thenReturn(Lists.newArrayList(opPost, opDelete, opGet));

        HeimdallRoute heimdallRoute = this.filter.getMatchingHeimdallRoute("/v2/api/foo", HttpMethod.OPTIONS.name(), this.ctx);
        assertNotNull(heimdallRoute);
        assertEquals("/api/foo", heimdallRoute.getRoute().getPath());
    }
}
