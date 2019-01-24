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

import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Scope;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.trace.FilterDetail;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.netflix.zuul.context.RequestContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static br.com.conductor.heimdall.gateway.util.ConstantsContext.CLIENT_ID;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.OPERATION_ID;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ScopesFilterTest {

    @InjectMocks
    private ScopesFilter filter;

    @Mock
    private AppRepository appRepository;

    private static RequestContext context;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private final String clientId1 = "client_id_1";
    private final String clientId2 = "client_id_2";

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
        TraceContextHolder.getInstance().init(true, "developer", this.request, false, "");

    }

    @Test
    public void clientIdNotAllowedInScope() {

        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId(1L);
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId(1L);

        Operation operation = new Operation(1111L, null, null,null, null, null);
        Scope scope = new Scope(1000L, null, null, null, null, null);

        app.setPlans(Lists.newArrayList(plan));
        plan.setScopes(Sets.newHashSet(scope));
        scope.setOperations(Sets.newHashSet(operation));

        context.set(OPERATION_ID, operation.getId() + 1);

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.FORBIDDEN.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void clientIdAllowedInScope() {

        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId(1L);
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId(2L);

        Operation operation = new Operation(1111L, null, null,null, null, null);
        Scope scope = new Scope(1000L, null, null, null, null, null);

        app.setPlans(Lists.newArrayList(plan));
        plan.setScopes(Sets.newHashSet(scope));
        scope.setOperations(Sets.newHashSet(operation));

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void multiplePlansSuccess() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Plan plan1 = new Plan();
        plan1.setId(100L);

        Plan plan2 = new Plan();
        plan2.setId(200L);

        App app1 = new App();
        app1.setId(10L);
        app1.setClientId(clientId1);
        app1.setPlans(Lists.newArrayList(plan1));

        App app2 = new App();
        app2.setId(20L);
        app2.setClientId(clientId2);
        app2.setPlans(Lists.newArrayList(plan2));

        Operation operation1 = new Operation(1111L, null, null,null, null, null);
        Operation operation2 = new Operation(2222L, null, null,null, null, null);

        Scope scope1 = new Scope();
        scope1.setId(123L);
        scope1.setOperations(Sets.newHashSet(operation1));

        Scope scope2 = new Scope();
        scope2.setId(321L);
        scope2.setOperations(Sets.newHashSet(operation2));

        plan1.setScopes(Sets.newHashSet(scope1));
        plan2.setScopes(Sets.newHashSet(scope2));

        context.set(OPERATION_ID, operation1.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app1);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void multiplePlansFailure() {
        this.request.addHeader(CLIENT_ID, clientId2);

        Plan plan1 = new Plan();
        plan1.setId(100L);

        Plan plan2 = new Plan();
        plan2.setId(200L);

        App app1 = new App();
        app1.setId(10L);
        app1.setClientId(clientId1);
        app1.setPlans(Lists.newArrayList(plan1));

        App app2 = new App();
        app2.setId(20L);
        app2.setClientId(clientId2);
        app2.setPlans(Lists.newArrayList(plan2));

        Operation operation1 = new Operation(1111L, null, null,null, null, null);
        Operation operation2 = new Operation(2222L, null, null,null, null, null);

        Scope scope1 = new Scope(123L, null, null, null, Sets.newHashSet(operation1), Sets.newHashSet(plan1));
        Scope scope2 = new Scope(321L, null, null, null, Sets.newHashSet(operation2), Sets.newHashSet(plan2));

        plan1.setScopes(Sets.newHashSet(scope1));
        plan2.setScopes(Sets.newHashSet(scope2));

        context.set(OPERATION_ID, operation1.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app2);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.FORBIDDEN.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());
    }

    @Test
    public void overlappingPlansApp1() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Plan plan1 = new Plan();
        plan1.setId(100L);

        Plan plan2 = new Plan();
        plan2.setId(200L);

        App app1 = new App();
        app1.setId(10L);
        app1.setClientId(clientId1);
        app1.setPlans(Lists.newArrayList(plan1));

        App app2 = new App();
        app2.setId(20L);
        app2.setClientId(clientId2);
        app2.setPlans(Lists.newArrayList(plan2));

        Operation operation1 = new Operation(1111L, null, null,null, null, null);
        Operation operation2 = new Operation(2222L, null, null,null, null, null);
        Operation operation3 = new Operation(3333L, null, null,null, null, null);

        Scope scope1 = new Scope(123L, null, null, null, Sets.newHashSet(operation1, operation3), Sets.newHashSet(plan1));
        Scope scope2 = new Scope(321L, null, null, null, Sets.newHashSet(operation2, operation3), Sets.newHashSet(plan2));

        plan1.setScopes(Sets.newHashSet(scope1));
        plan2.setScopes(Sets.newHashSet(scope2));

        context.set(OPERATION_ID, operation3.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app1);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());
    }

    @Test
    public void overlappingPlansApp2() {
        this.request.addHeader(CLIENT_ID, clientId1);

        Plan plan1 = new Plan();
        plan1.setId(100L);

        Plan plan2 = new Plan();
        plan2.setId(200L);

        App app1 = new App();
        app1.setId(10L);
        app1.setClientId(clientId1);
        app1.setPlans(Lists.newArrayList(plan1));

        Operation operation1 = new Operation(1111L, null, null,null, null, null);
        Operation operation2 = new Operation(2222L, null, null,null, null, null);
        Operation operation3 = new Operation(3333L, null, null,null, null, null);

        Scope scope1 = new Scope(123L, null, null, null, Sets.newHashSet(operation1, operation3), Sets.newHashSet(plan1));
        Scope scope2 = new Scope(321L, null, null, null, Sets.newHashSet(operation2, operation3), Sets.newHashSet(plan2));

        plan1.setScopes(Sets.newHashSet(scope1));
        plan2.setScopes(Sets.newHashSet(scope2));

        context.set(OPERATION_ID, operation2.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app1);

        this.filter.run();

        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.FORBIDDEN.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());
    }

    @Test
    public void noClientIdHeader() {

        App app = new App();
        app.setId(1L);
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId(1L);

        Operation operation = new Operation(1111L, null, null,null, null, null);
        Scope scope = new Scope(1000L, null, null, null, null, null);

        app.setPlans(Lists.newArrayList(plan));
        plan.setScopes(Sets.newHashSet(scope));
        scope.setOperations(Sets.newHashSet(operation));

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noApp() {

        this.request.addHeader(CLIENT_ID, clientId1);

        Operation operation = new Operation(1111L, null, null,null, null, null);
        Scope scope = new Scope(1000L, null, null, null, null, null);

        scope.setOperations(Sets.newHashSet(operation));

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noPlan() {
        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId(1L);
        app.setClientId(clientId1);

        Operation operation = new Operation(1111L, null, null,null, null, null);
        Scope scope = new Scope(1000L, null, null, null, null, null);

        scope.setOperations(Sets.newHashSet(operation));

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noScopes() {
        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId(1L);
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId(1L);

        Operation operation = new Operation(1111L, null, null,null, null, null);

        context.set(OPERATION_ID, operation.getId());

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }

    @Test
    public void noOperationIdContext() {
        this.request.addHeader(CLIENT_ID, clientId1);

        App app = new App();
        app.setId(1L);
        app.setClientId(clientId1);

        Plan plan = new Plan();
        plan.setId(1L);

        Operation operation = new Operation(1111L, null, null,null, null, null);
        Scope scope = new Scope(1000L, null, null, null, null, null);

        app.setPlans(Lists.newArrayList(plan));
        plan.setScopes(Sets.newHashSet(scope));
        scope.setOperations(Sets.newHashSet(operation));

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);

        this.filter.run();
        final FilterDetail filterDetail = TraceContextHolder.getInstance().getActualTrace().getFilters().get(0);

        assertEquals(HttpStatus.OK.value(), context.getResponseStatusCode());
        assertEquals(Constants.SUCCESS, filterDetail.getStatus());

    }
}
