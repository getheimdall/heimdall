/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */


package br.com.heimdall.core.service;

import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.ScopeDTO;
import br.com.heimdall.core.dto.page.ScopePage;
import br.com.heimdall.core.entity.*;
import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.entity.Operation;
import br.com.heimdall.core.entity.Plan;
import br.com.heimdall.core.entity.Resource;
import br.com.heimdall.core.entity.Scope;
import br.com.heimdall.core.enums.HttpMethod;
import br.com.heimdall.core.enums.Status;
import br.com.heimdall.core.exception.BadRequestException;
import br.com.heimdall.core.exception.NotFoundException;
import br.com.heimdall.core.repository.OperationRepository;
import br.com.heimdall.core.repository.ScopeRepository;
import br.com.heimdall.core.service.amqp.AMQPCacheService;
import br.com.heimdall.core.util.Pageable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.*;

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
    private OperationRepository operationRepository;

    @Mock
    private AMQPCacheService amqpCacheService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Scope scope;

    private Operation operation;
    private Operation operation2;
    private Operation operation3;

    @Before
    public void before() {

        Api api = new Api();
        api.setId(1L);
        api.setBasePath("/api");
        api.setStatus(Status.ACTIVE);

        List<Plan> plans = new ArrayList<>();

        Plan plan = new Plan();
        plan.setName("plan");
        plan.setDefaultPlan(true);
        plan.setApi(api);
        plans.add(plan);

        api.setPlans(plans);

        Resource resource = new Resource();
        resource.setId(1L);
        resource.setApi(api);
        resource.setName("resource");

        operation = new Operation();
        operation.setId(1L);
        operation.setResource(resource);
        operation.setPath("/operation-1");
        operation.setMethod(HttpMethod.GET);

        operation2 = new Operation();
        operation2.setId(2L);
        operation2.setResource(resource);
        operation2.setPath("/operation-2");
        operation2.setMethod(HttpMethod.POST);

        operation3 = new Operation();
        operation3.setId(3L);
        operation3.setResource(resource);
        operation3.setPath("/operation-3");
        operation3.setMethod(HttpMethod.GET);

        List<Operation> operations = new ArrayList<>();
        operations.add(operation);
        operations.add(operation2);
        operations.add(operation3);

        Set<Operation> operationsSet = new HashSet<>();
        operationsSet.add(operation);
        operationsSet.add(operation2);

        resource.setOperations(operations);

        Set<Resource> resources = new HashSet<>();
        resources.add(resource);
        api.setResources(resources);

        scope = new Scope();
        scope.setId(1L);
        scope.setApi(api);
        scope.setDescription("Scope description");
        scope.setName("Scope");
        scope.setOperations(operationsSet);
    }

    @Test
    public void findTest() {
        Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(scope);
        Scope scopeActual = scopeService.find(this.scope.getApi().getId(), this.scope.getId());

        assertEquals(scope, scopeActual);
    }

    @Test
    public void findWithNotFoundTest() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Resource not found");

        Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        Scope scopeActual = scopeService.find(this.scope.getApi().getId(), this.scope.getId());

        assertEquals(scope, scopeActual);
    }

    @Test
    public void listTest() {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName("Scope");
        scopeDTO.setDescription("Scope description");

        List<Scope> scopesExpected = new ArrayList<>();
        scopesExpected.add(scope);

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findAll(Mockito.any(Example.class))).thenReturn(scopesExpected);

        List<Scope> listScopes = scopeService.list(scope.getApi().getId(), scopeDTO);

        assertEquals(scopesExpected, listScopes);
    }

    @Test
    public void listPageTest() {
        ScopeDTO scopeDTO = new ScopeDTO();
        scopeDTO.setName("Scope");
        scopeDTO.setDescription("Scope description");
        List<Scope> scopesExpected = new ArrayList<>();
        scopesExpected.add(scope);

        Page<Scope> scopes = createPageScope(scopesExpected);

        PageableDTO pageableDTO = new PageableDTO();
        pageableDTO.setLimit(10);
        pageableDTO.setOffset(0);

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class))).thenReturn(scopes);

        ScopePage scopePage = scopeService.list(scope.getApi().getId(), scopeDTO, pageableDTO);

        Assert.assertEquals(scopes.getContent(), scopePage.getContent());
    }

    @Test
    public void saveTest() {

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        Mockito.when(scopeRepository.save(Mockito.any(Scope.class))).thenReturn(scope);
        Mockito.when(operationRepository.findOne(1L)).thenReturn(operation);
        Mockito.when(operationRepository.findOne(2L)).thenReturn(operation2);
        Mockito.when(operationRepository.findOne(3L)).thenReturn(operation3);

        Scope scopeSaved = scopeService.save(scope.getApi().getId(), scope);

        assertEquals(scope, scopeSaved);
    }

    @Test
    public void saveWithApiNotFoundTest() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Resource not found");

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(null);

        scopeService.save(scope.getApi().getId(), scope);
    }

    @Test
    public void saveWithNameAlreadyExistTest() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("A Scope with the provided name already exists");

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(scope);

        scopeService.save(scope.getApi().getId(), scope);
    }

    @Test
    public void saveWithoutOperationTest() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("A Scope must have at least one Operation");

        scope.setOperations(null);
        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);

        scopeService.save(scope.getApi().getId(), scope);
    }

    @Test
    public void saveWithOperationNullTest() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Operation with id '1' does not exist");

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        Mockito.when(operationRepository.findOne(Mockito.anyLong())).thenReturn(null);

        scopeService.save(scope.getApi().getId(), scope);
    }

    @Test
    public void saveWithOperationNotInApi() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Operation '1' not in Api '1'");

        Set<Operation> operations = new HashSet<>();
        Set<Resource> resources = new HashSet<>();

        Resource resource = new Resource();
        Operation operation = new Operation();
        operation.setId(1L);
        operation.setResource(resource);

        resources.add(resource);

        Api api = new Api();
        api.setId(2L);
        api.setResources(resources);
        resource.setApi(api);
        operations.add(operation);
        scope.setOperations(operations);

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndName(Mockito.anyLong(), Mockito.anyString())).thenReturn(null);
        Mockito.when(operationRepository.findOne(Mockito.anyLong())).thenReturn(operation);

        scopeService.save(scope.getApi().getId(), scope);
    }


    @Test
    public void deleteTest() {

        Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(scope);

        scopeService.delete(scope.getApi().getId(), scope.getId());

        Mockito.verify(scopeRepository, Mockito.times(1)).findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void deleteWithNotFoundTest() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Resource not found");
        Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        scopeService.delete(scope.getApi().getId(), scope.getId());
    }

    @Test
    public void updateTest() {

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(scope);

        scope.setName("Scope new name");
        Mockito.when(scopeRepository.save(scope)).thenReturn(scope);
        Scope updated = scopeService.update(scope.getApi().getId(), scope.getId(), scope);

        assertEquals(scope.getName(), updated.getName());
    }

    @Test
    public void updateTestWithNotFoundTest() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Resource not found");

        Mockito.when(apiService.find(Mockito.anyLong())).thenReturn(scope.getApi());
        Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);

        scope.setName("Scope new name");
        Mockito.when(scopeRepository.save(scope)).thenReturn(scope);
        Scope updated = scopeService.update(scope.getApi().getId(), scope.getId(), scope);

        assertEquals(scope.getName(), updated.getName());
    }

    private Page<Scope> createPageScope(List<Scope> scopes) {
        return new Page<Scope>() {
            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <S> Page<S> map(Converter<? super Scope, ? extends S> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 1;
            }

            @Override
            public int getNumberOfElements() {
                return 1;
            }

            @Override
            public List<Scope> getContent() {
                return scopes;
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Scope> iterator() {
                return null;
            }
        };
    }
}
