package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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

import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.PlanDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.dto.ScopeDTO;
import br.com.conductor.heimdall.core.dto.page.PlanPage;
import br.com.conductor.heimdall.core.dto.page.ScopePage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.entity.Scope;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.repository.ScopeRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://github.com/cassioesp" target="_blank">Cássio Espíndola/a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ScopeServiceTest {

     @InjectMocks
     private ScopeService scopeService;

     @Mock
     private ApiService apiService;

     @Mock
     private ScopeRepository scopeRepository;

     @Mock
     private OperationRepository operationRepository;

     @Mock
     private AMQPRouteService amqpRoute;

     @Mock
     private AMQPCacheService amqpCacheService;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private ScopeDTO scopeDTO;

     private Scope scope;

     private Api api;

     private Set<Operation> operations;

     private Operation operation;

     private Operation operation2;

     private Resource resource;

     @Before
     public void initAttributes() {

          api = new Api();
          api.setId(1L);
          api.setBasePath("/test");
          api.setName("test");
          api.setDescription("test");
          api.setVersion("1.0.0");
          api.setStatus(Status.ACTIVE);

          scope = new Scope();
          scope.setId(1L);
          scope.setName("My Plan");
          scope.setDescription("My Plan Description");
          operations = new HashSet<>();
          operation = new Operation();
          resource = new Resource();
          resource.setId(1L);
          resource.setApi(api);
          operation.setId(1L);
          operation.setResource(resource);
          operation2 = new Operation();
          operation2.setId(2L);
          operation2.setResource(resource);
          operations.add(operation);
          operations.add(operation2);
          scope.setOperations(operations);
          scope.setApi(api);

          scopeDTO = new ScopeDTO();
          scopeDTO.setName("My Plan");
          scopeDTO.setDescription("My Plan Description");
     }

     @Test
     public void saveScope() {

          Mockito.when(apiService.find(1L)).thenReturn(api);

          Mockito.when(scopeRepository.save(Mockito.any(Scope.class))).thenReturn(scope);
          Mockito.when(operationRepository.findOne(Mockito.anyLong())).thenReturn(operation);
          Mockito.when(scopeRepository.findByApiIdAndName(Mockito.any(Long.class), Mockito.anyString()))
                 .thenReturn(null);

          Scope saved = scopeService.save(api.getId(), scope);

          assertEquals(saved.getId(), scope.getId());
     }

     @Test
     public void findScope() {

          Mockito.when(scopeRepository.findOne(Mockito.any(Long.class))).thenReturn(scope);
          Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong()))
                 .thenReturn(scope);
          Scope scopeResp = scopeService.find(1L, 1L);
          assertEquals(scopeResp.getId(), scope.getId());
          Mockito.verify(this.scopeRepository, Mockito.times(1))
                 .findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong());
     }

     @Test
     public void listScopes() {

          this.scope.setName("Scope Name");

          List<Scope> scopes = new ArrayList<>();
          scopes.add(scope);

          Mockito.when(this.scopeRepository.findAll(Mockito.any(Example.class))).thenReturn(scopes);

          List<Scope> planResp = this.scopeService.list(1L, this.scopeDTO);

          assertEquals(scopes.size(), planResp.size());
          Mockito.verify(this.scopeRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listScopesWithPageable() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setOffset(0);

          ArrayList<Scope> listScopes = new ArrayList<>();

          scope.setName("Scope Name");

          listScopes.add(scope);

          Page<Scope> page = new PageImpl<>(listScopes);

          Mockito.when(this.scopeRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                 .thenReturn(page);

          ScopePage scopePageResp = this.scopeService.list(1L, this.scopeDTO, pageableDTO);

          assertEquals(1L, scopePageResp.getTotalElements());
          Mockito.verify(this.scopeRepository, Mockito.times(1))
                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
     }

     @Test
     public void updateScope() {

          Api api = new Api();
          api.setId(1L);
          api.setBasePath("/test");
          api.setName("test");
          api.setDescription("test");
          api.setVersion("1.0.0");
          api.setStatus(Status.ACTIVE);
          Mockito.when(apiService.find(1L)).thenReturn(api);
          Mockito.when(scopeRepository.findOne(Mockito.anyLong())).thenReturn(scope);
          Mockito.when(scopeRepository.save(Mockito.any(Scope.class))).thenReturn(scope);
          Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(scope);
          Mockito.when(scopeRepository.findByApiIdAndName(Mockito.any(Long.class), Mockito.anyString())).thenReturn(null);
          Mockito.when(operationRepository.findOne(Mockito.anyLong())).thenReturn(operation);

          Scope saved = scopeService.save(1L, scope);

          Scope update = scopeService.update(1L, 1L, scope);

          assertEquals(update.getId(), scope.getId());
     }

          @Test
          public void deleteScope() {
               Mockito.when(scopeRepository.findByApiIdAndId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(scope);
               Mockito.when(scopeRepository.findOne(Mockito.anyLong())).thenReturn(scope);
               this.scopeService.delete(1L, 1L);
               Mockito.verify(this.scopeRepository, Mockito.times(1)).delete(scope);

          }
}
