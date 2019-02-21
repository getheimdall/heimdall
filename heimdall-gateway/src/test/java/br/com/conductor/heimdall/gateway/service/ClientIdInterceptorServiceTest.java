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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.Location;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static br.com.conductor.heimdall.core.util.Constants.INTERRUPT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
public class ClientIdInterceptorServiceTest {

    @InjectMocks
    private ClientIdInterceptorService clientIdInterceptorService;

    @Mock
    private AppRepository appRepository;

    private RequestContext ctx;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private String clientId;

    private String someOtherClientId;

    private Api api1;

    private Api api2;

    private App app;

    @Before
    public void initTest() {
        MockitoAnnotations.initMocks(this);

        ctx = RequestContext.getCurrentContext();
        ctx.clear();
        ctx.setRequest(this.request);
        ctx.setResponse(this.response);
        TraceContextHolder.getInstance().init(true, "developer", request, false, false);

        clientId = "simpleId";
        someOtherClientId = "someOtherClientId";

        this.request.addHeader("client_id", clientId);

        api1 = new Api();
        api1.setId(10L);

        api2 = new Api();
        api2.setId(20L);

        Plan plan1 = new Plan();
        plan1.setApi(api1);

        Plan plan2 = new Plan();
        plan2.setApi(api2);

        List<Plan> planList = Arrays.asList(plan1, plan2);

        Developer developer = new Developer();
        developer.setEmail("some@email.com");

        app = new App();
        app.setPlans(planList);
        app.setDeveloper(developer);
        app.setClientId(clientId);

    }

    @Test
    public void successCaseTest() {

        Mockito.when(appRepository.findByClientId(clientId)).thenReturn(app);

        clientIdInterceptorService.validate(api1.getId(), Location.HEADER);

        // Internal Server Error is expected because the request has no finished at this
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), this.ctx.getResponseStatusCode());
    }

    @Test
    public void successCase2Test() {

        Mockito.when(appRepository.findByClientId(clientId)).thenReturn(app);

        clientIdInterceptorService.validate(api2.getId(), Location.HEADER);

        // Internal Server Error is expected because the request has no finished at this
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), this.ctx.getResponseStatusCode());
    }

    @Test
    public void clientIdNullTest() {

        clientIdInterceptorService.validate( 10L, null);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), this.ctx.getResponseStatusCode());
        assertTrue((Boolean) this.ctx.get(INTERRUPT));
        assertFalse(ctx.sendZuulResponse());
    }

    @Test
    public void apiIdNullTest() {

        clientIdInterceptorService.validate(null, Location.HEADER);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), this.ctx.getResponseStatusCode());
        assertTrue((Boolean) this.ctx.get(INTERRUPT));
        assertFalse(ctx.sendZuulResponse());

    }

    @Test
    public void unauthorizedClientIdTest() {

        this.request.addHeader("client_id", someOtherClientId);

        Mockito.when(appRepository.findByClientId(someOtherClientId)).thenReturn(null);

        clientIdInterceptorService.validate(api1.getId(), Location.HEADER);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), this.ctx.getResponseStatusCode());
        assertTrue((Boolean) this.ctx.get(INTERRUPT));
        assertFalse(ctx.sendZuulResponse());

    }

    @Test
    public void apiNotInPlanTest() {

        Api api = new Api();
        api.setId(30L);

        Mockito.when(appRepository.findByClientId(clientId)).thenReturn(app);

        clientIdInterceptorService.validate(api.getId(), Location.HEADER);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), this.ctx.getResponseStatusCode());
        assertTrue((Boolean) this.ctx.get(INTERRUPT));
        assertFalse(ctx.sendZuulResponse());
    }

    @Test
    public void planNotInAppTest() {

        Api api = new Api();
        api.setId(30L);

        Plan plan = new Plan();
        plan.setApi(api);

        Mockito.when(appRepository.findByClientId(clientId)).thenReturn(app);

        clientIdInterceptorService.validate(api.getId(), Location.HEADER);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), this.ctx.getResponseStatusCode());
        assertTrue((Boolean) this.ctx.get(INTERRUPT));
        assertFalse(ctx.sendZuulResponse());
    }
}
