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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.ScopeDTO;
import br.com.conductor.heimdall.core.entity.*;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.exception.NotFoundException;
import br.com.conductor.heimdall.core.repository.ScopeRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 **/
@RunWith(MockitoJUnitRunner.class)
public class ScopeServiceTest {

    @InjectMocks
    private ScopeService scopeService;

    @Mock
    private ScopeRepository scopeRepository;

    @Mock
    private ApiService apiService;

    @Mock
    private OperationService operationService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Scope scope;

    private Operation operation;
    private Operation operation2;

    private Api api;

    @Before
    public void before() {

        api = new Api();
        api.setId("1L");
        api.setBasePath("/api");
        api.setStatus(Status.ACTIVE);

        Set<String> plans = new HashSet<>();

        Plan plan = new Plan();
        plan.setName("plan");
        plan.setDefaultPlan(true);
        plan.setApiId(api.getId());
        plans.add(plan.getId());

        api.setPlans(plans);

        Resource resource = new Resource();
        resource.setId("1L");
        resource.setApiId(api.getId());
        resource.setName("resource");

        operation = new Operation();
        operation.setId("1L");
        operation.setResourceId(resource.getId());
        operation.setPath("/operation-1");
        operation.setMethod(HttpMethod.GET);
        operation.setApiId(api.getId());

        operation2 = new Operation();
        operation2.setId("2L");
        operation2.setResourceId(resource.getId());
        operation2.setPath("/operation-2");
        operation2.setMethod(HttpMethod.POST);
        operation2.setApiId(api.getId());

        Operation operation3 = new Operation();
        operation3.setId("3L");
        operation3.setResourceId(resource.getId());
        operation3.setPath("/operation-3");
        operation3.setMethod(HttpMethod.GET);
        operation3.setApiId(api.getId());

        List<String> operations = Lists.newArrayList(operation.getId(), operation2.getId(), operation3.getId());

        Set<String> operationsSet = Sets.newSet(operation.getId(), operation2.getId());

        resource.setOperations(operations);

        Set<String> resources = new HashSet<>();
        resources.add(resource.getId());
        api.setResources(resources);

        scope = new Scope();
        scope.setId("1L");
        scope.setApi(api.getId());
        scope.setDescription("Scope description");
        scope.setName("Scope");
        scope.setOperations(operationsSet);
    }

    @Test
    public void findTest() {
        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(scope);
        Scope scopeActual = scopeService.find(this.scope.getApi(), this.scope.getId());

        assertEquals(scope, scopeActual);
    }

    @Test
    public void findWithNotFoundTest() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Scope not found");

        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Scope scopeActual = scopeService.find(this.scope.getApi(), this.scope.getId());

        assertEquals(scope, scopeActual);
    }

    @Test
    public void listTest() {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName("Scope");
        scopeDTO.setDescription("Scope description");

        List<Scope> scopesExpected = new ArrayList<>();
        scopesExpected.add(scope);

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findAll()).thenReturn(scopesExpected);

        List<Scope> listScopes = scopeService.list(scope.getApi());

        assertEquals(scopesExpected, listScopes);
    }

    @Test
    public void listPageTest() {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName("Scope");
        scopeDTO.setDescription("Scope description");
        List<Scope> scopesExpected = new ArrayList<>();
        scopesExpected.add(scope);

        Page<Scope> scopes = new PageImpl<>(scopesExpected);

        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findAll()).thenReturn(scopesExpected);

        Page<Scope> scopePage = scopeService.list(scope.getApi(), pageable);

        assertEquals(scopes.getContent(), scopePage.getContent());
    }

    @Test
    public void saveTest() {

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findByApiAndName(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(scopeRepository.save(Mockito.any(Scope.class))).thenReturn(scope);
        Mockito.when(operationService.find("1L")).thenReturn(operation);
        Mockito.when(operationService.find("2L")).thenReturn(operation2);

        Scope scopeSaved = scopeService.save(scope.getApi(), scope);

        assertEquals(scope, scopeSaved);
    }

    @Test
    public void saveWithNameAlreadyExistTest() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("A Scope with the provided name already exists");

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findByApiAndName(Mockito.anyString(), Mockito.anyString())).thenReturn(scope);

        scopeService.save(scope.getApi(), scope);
    }

    @Test
    public void saveWithoutOperationTest() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("A Scope must have at least one Operation");

        scope.setOperations(null);
        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findByApiAndName(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        scopeService.save(scope.getApi(), scope);
    }

    @Test
    public void saveWithOperationNullTest() {

        thrown.expect(BadRequestException.class);

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findByApiAndName(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(operationService.find(Mockito.anyString())).thenReturn(null);

        scopeService.save(scope.getApi(), scope);
    }

    @Test
    public void saveWithOperationNotInApi() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Operation '10L' not in Api '1L'");

        Set<String> operations = new HashSet<>();
        Set<String> resources = new HashSet<>();

        Resource resource = new Resource();
        resource.setId("1L");
        Operation operation = new Operation();
        operation.setId("10L");
        operation.setResourceId(resource.getId());

        resources.add(resource.getId());

        Api api = new Api();
        api.setId("2L");
        api.setResources(resources);
        resource.setApiId(api.getId());
        operations.add(operation.getId());
        scope.setOperations(operations);

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(this.api);
        Mockito.when(scopeRepository.findByApiAndName(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Mockito.when(operationService.find(Mockito.anyString())).thenReturn(operation);

        scopeService.save(scope.getApi(), scope);
    }

    @Test
    public void deleteTest() {

        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(scope);

        scopeService.delete(scope.getApi(), scope.getId());

        Mockito.verify(scopeRepository, Mockito.times(1)).findByApiAndId(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void deleteWithNotFoundTest() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Scope not found");
        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        scopeService.delete(scope.getApi(), scope.getId());
    }

    @Test
    public void updateTest() {

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(scope);
        Mockito.when(operationService.find(Mockito.anyString())).thenReturn(operation);

        scope.setName("Scope new name");
        Mockito.when(scopeRepository.save(scope)).thenReturn(scope);
        Scope updated = scopeService.update(scope.getApi(), scope.getId(), scope);

        assertEquals(scope.getName(), updated.getName());
    }

    @Test
    public void updateTestWithNotFoundTest() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Scope not found");

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(scopeRepository.findByApiAndId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        scope.setName("Scope new name");
//        Mockito.when(scopeRepository.save(scope)).thenReturn(scope);
        Scope updated = scopeService.update(scope.getApi(), scope.getId(), scope);

        assertEquals(scope.getName(), updated.getName());
    }

}
