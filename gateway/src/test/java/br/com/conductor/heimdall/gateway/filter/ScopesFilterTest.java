/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.gateway.filter;

import br.com.conductor.heimdall.core.entity.*;
import br.com.conductor.heimdall.core.repository.ScopeRepository;
import br.com.conductor.heimdall.core.service.AppService;
import br.com.conductor.heimdall.core.service.PlanService;
import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.core.trace.FilterDetail;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.*;

import static br.com.conductor.heimdall.gateway.util.ConstantsContext.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ScopesFilterTest {

    @InjectMocks
    private ScopesFilter filter;

    @Mock
    private AppService appService;

    @Mock
    private PlanService planService;

    @Mock
    private ScopeRepository scopeRepository;

    private static RequestContext context;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private final String clientId1 = "client_id_1";
    private final String clientId2 = "client_id_2";
    private final String SCOPES_FILTER = "ScopesFilter";

    @BeforeClass
    public static void setUp() {
        context = RequestContext.getCurrentContext();
    }

    @Before
    public void init() {

        context.clear();
        context.setRequest(this.request);
        context.setResponse(this.response);
        context.setResponseStatusCode(HttpStatus.OK.value());
        TraceContextHolder.getInstance().init(true,"developer", this.request,false, true);

    }

    @Test
    public void clientIdNotInScope() {

        this.request.addHeader(CLIENT_ID, clientId1);

        Api api = new Api();
        api.setId("1L");

        App app = new App();
        app.setId("10L");
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId("20L");
        plan.setApiId(api.getId());

        Operation operation = new Operation("1111L", null, null,null, null, null);
        Scope scope = new Scope("1000L", null, null, null, null);

        app.setPlans(Sets.newSet("20L"));
        plan.setScopes(Sets.newSet(scope.getId()));
        scope.setOperations(Sets.newSet(operation.getId()));

        context.set(OPERATION_ID, operation.getId() + 1);

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app);
        Mockito.when(planService.find(plan.getId())).thenReturn(plan);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void clientIdAllowedInScope() {

        this.request.addHeader(CLIENT_ID, clientId1);

        Api api = new Api();
        api.setId("1L");

        App app = new App();
        app.setId("10L");
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId("20L");
        plan.setApiId(api.getId());

        Operation operation = new Operation("1111L", null, null,null, null, null);
        Scope scope = new Scope("1000L", null, null, null, null);

        app.setPlans(Sets.newSet(plan.getId()));
        plan.setScopes(Sets.newSet(scope.getId()));
        scope.setOperations(Sets.newSet(operation.getId()));

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app);
        Mockito.when(planService.find(plan.getId())).thenReturn(plan);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void multiplePlansSuccess() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Api api1 = new Api();
        api1.setId("1L");

        Api api2 = new Api();
        api2.setId("2L");

        Plan plan1 = new Plan();
        plan1.setId("100L");
        plan1.setApiId(api1.getId());

        Plan plan2 = new Plan();
        plan2.setId("200L");
        plan2.setApiId(api2.getId());

        App app1 = new App();
        app1.setId("10L");
        app1.setClientId(clientId1);
        app1.setPlans(Sets.newSet(plan1.getId()));

        App app2 = new App();
        app2.setId("20L");
        app2.setClientId(clientId2);
        app2.setPlans(Sets.newSet(plan2.getId()));

        Operation operation1 = new Operation("1111L", null, null, null,null, null);
        Operation operation2 = new Operation("2222L", null, null, null,null, null);

        Scope scope1 = new Scope();
        scope1.setId("123L");
        scope1.setOperations(Sets.newSet(operation1.getId()));

        Scope scope2 = new Scope();
        scope2.setId("321L");
        scope2.setOperations(Sets.newSet(operation2.getId()));

        plan1.setScopes(Sets.newSet(scope1.getId()));
        plan2.setScopes(Sets.newSet(scope2.getId()));

        context.set(OPERATION_ID, operation1.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app2);
        Mockito.when(planService.find(plan2.getId())).thenReturn(plan2);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void multiplePlansFailure() {
        this.request.addHeader(CLIENT_ID, clientId2);

        Api api1 = new Api();
        api1.setId("1L");

        Api api2 = new Api();
        api2.setId("2L");

        Plan plan1 = new Plan();
        plan1.setId("100L");
        plan1.setApiId(api1.getId());

        Plan plan2 = new Plan();
        plan2.setId("200L");
        plan2.setApiId(api2.getId());

        App app1 = new App();
        app1.setId("10L");
        app1.setClientId(clientId1);
        app1.setPlans(Sets.newSet(plan1.getId()));

        App app2 = new App();
        app2.setId("20L");
        app2.setClientId(clientId2);
        app2.setPlans(Sets.newSet(plan2.getId()));

        Operation operation1 = new Operation("1111L", null, null, null,null, null);
        Operation operation2 = new Operation("2222L", null, null, null,null, null);

        Scope scope1 = new Scope("123L", null, null, null, Sets.newSet(operation1.getId()));
        Scope scope2 = new Scope("321L", null, null, null, Sets.newSet(operation2.getId()));

        plan1.setScopes(Sets.newSet(scope1.getId()));
        plan2.setScopes(Sets.newSet(scope2.getId()));

        context.set(OPERATION_ID, operation1.getId());
        context.set(API_ID, api2.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app2);
        Mockito.when(planService.find(plan2.getId())).thenReturn(plan2);
        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(scope2);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.FORBIDDEN.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());
    }

    @Test
    public void overlappingPlansApp1() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Api api1 = new Api();
        api1.setId("1L");

        Api api2 = new Api();
        api2.setId("2L");

        Plan plan1 = new Plan();
        plan1.setId("100L");
        plan1.setApiId(api1.getId());

        Plan plan2 = new Plan();
        plan2.setId("200L");
        plan2.setApiId(api2.getId());

        App app1 = new App();
        app1.setId("10L");
        app1.setClientId(clientId1);
        app1.setPlans(Sets.newSet(plan1.getId()));

        App app2 = new App();
        app2.setId("20L");
        app2.setClientId(clientId2);
        app2.setPlans(Sets.newSet(plan2.getId()));

        Operation operation1 = new Operation("1111L", null, null, null,null, null);
        Operation operation2 = new Operation("2222L", null, null, null,null, null);
        Operation operation3 = new Operation("3333L", null, null, null,null, null);

        Set<String> operations1 = new HashSet<>();
        Set<String> operations2 = new HashSet<>();

        operations1.add(operation1.getId());
        operations1.add(operation3.getId());

        operations2.add(operation2.getId());
        operations2.add(operation3.getId());

        Scope scope1 = new Scope("123L", null, null, null, operations1);
        Scope scope2 = new Scope("321L", null, null, null, operations2);

        plan1.setScopes(Sets.newSet(scope1.getId()));
        plan2.setScopes(Sets.newSet(scope2.getId()));

        context.set(OPERATION_ID, operation3.getId());
        context.set(API_ID, api1.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app1);
        Mockito.when(planService.find(plan1.getId())).thenReturn(plan1);
        Mockito.when(scopeRepository.findByApiAndId(api1.getId(), scope1.getId())).thenReturn(scope1);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());
    }

    @Test
    public void overlappingPlansApp2() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Api api1 = new Api();
        api1.setId("1L");

        Api api2 = new Api();
        api2.setId("2L");

        Plan plan1 = new Plan();
        plan1.setId("100L");
        plan1.setApiId(api1.getId());

        Plan plan2 = new Plan();
        plan2.setId("200L");
        plan2.setApiId(api2.getId());

        App app1 = new App();
        app1.setId("10L");
        app1.setClientId(clientId1);
        app1.setPlans(Sets.newSet(plan1.getId()));

        Operation operation1 = new Operation("1111L", null, null, null,null, null);
        Operation operation2 = new Operation("2222L", null, null, null,null, null);
        Operation operation3 = new Operation("3333L", null, null, null,null, null);

        Set<String> operations1 = new HashSet<>();
        Set<String> operations2 = new HashSet<>();

        operations1.add(operation1.getId());
        operations1.add(operation3.getId());

        operations2.add(operation2.getId());
        operations2.add(operation3.getId());

        Scope scope1 = new Scope("123L", null, null, null, operations1);
        Scope scope2 = new Scope("321L", null, null, null, operations2);

        plan1.setScopes(Sets.newSet(scope1.getId()));
        plan2.setScopes(Sets.newSet(scope2.getId()));

        context.set(OPERATION_ID, operation2.getId());
        context.set(API_ID, api1.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app1);
        Mockito.when(planService.find(plan1.getId())).thenReturn(plan1);
        Mockito.when(scopeRepository.findByApiAndId(api1.getId(), scope1.getId())).thenReturn(scope1);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.FORBIDDEN.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());
    }

    @Test
    public void noClientIdHeader() {

        Api api = new Api();
        api.setId("1L");

        App app = new App();
        app.setId("100L");
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId("10L");
        plan.setApiId(api.getId());

        Operation operation = new Operation("1111L", null, null, null,null, null);
        Scope scope = new Scope("1000L", null, null, null, null);

        app.setPlans(Sets.newSet(plan.getId()));
        plan.setScopes(Sets.newSet(scope.getId()));
        scope.setOperations(Sets.newSet(operation.getId()));

        context.set(OPERATION_ID, operation.getId());
        context.set(API_ID, api.getId());

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noApp() {

        this.request.addHeader(CLIENT_ID, clientId1);

        Api api = new Api();
        api.setId("1L");

        Operation operation = new Operation("1111L", null, null, null,null, null);
        Scope scope = new Scope("1000L", null, null, null, null);

        scope.setOperations(Sets.newSet(operation.getId()));

        context.set(OPERATION_ID, operation.getId());
        context.set(API_ID, api.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(null);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noPlan() {
        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId("1L");
        app.setClientId(clientId1);

        Operation operation = new Operation("1111L", null, null, null,null, null);
        Scope scope = new Scope("1000L", null, null, null, null);

        scope.setOperations(Sets.newSet(operation.getId()));

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noScopes() {
        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId("1L");
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId("1L");

        Operation operation = new Operation("1111L", null, null, null,null, null);

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noOperationIdContext() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Api api = new Api();
        api.setId("1L");

        App app = new App();
        app.setId("100L");
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId("10L");
        plan.setApiId(api.getId());

        Operation operation = new Operation("1111L", null, null, null,null, null);
        Scope scope = new Scope("1000L", null, null, null, null);

        app.setPlans(Sets.newSet(plan.getId()));
        plan.setScopes(Sets.newSet(scope.getId()));
        scope.setOperations(Sets.newSet(operation.getId()));

        Mockito.when(appService.findByClientId(Mockito.anyString())).thenReturn(app);
        Mockito.when(planService.find(Mockito.anyString())).thenReturn(plan);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(SCOPES_FILTER);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }
}
