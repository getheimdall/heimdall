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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.publisher.RedisRoutePublisher;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class OperationServiceTest {

    @InjectMocks
    private OperationService operationService;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ApiService apiService;

    @Mock
    private InterceptorService interceptorService;

    @Mock
    private RedisRoutePublisher redisRoutePublisher;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Operation operation;

    private Resource res;

    private Api api;

    private Operation operationDTO;

    @Before
    public void initAttributes() {

        Set<String> resources = new HashSet<>();

        api = new Api();
        api.setId("1L");
        api.setBasePath("http://127.0.0.1");
        api.setResources(resources);

        res = new Resource();
        res.setId("1L");
        res.setApiId(api.getId());
        resources.add(res.getId());

        operation = new Operation();
        operation.setId("1L");
        operation.setMethod(HttpMethod.GET);
        operation.setResourceId(res.getId());
        operation.setDescription("Operation Description");
        operation.setPath("/test");

        operationDTO = new Operation();
        operationDTO.setDescription("Operation Description");
        operationDTO.setMethod(HttpMethod.GET);
        operationDTO.setPath("/test");

    }

    @Test
    public void saveOperation() {

        Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);
        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);

        Operation saved = operationService.save(api.getId(), res.getId(), operation);
        assertEquals(saved.getId(), operation.getId());
    }

    @Test
    public void findOperation() {

        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);
        Mockito.when(operationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(operation));
        Operation opearationResp = operationService.find("1L", "1L", "1L");

        assertEquals(opearationResp.getId(), operation.getId());
        Mockito.verify(this.operationRepository, Mockito.times(1))
                .findById(Mockito.anyString());
    }

    @Test
    public void listOperations() {

        List<Operation> operations = new ArrayList<>();
        operations.add(operation);
        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);
        Mockito.when(this.operationRepository.findAll()).thenReturn(operations);

        List<Operation> operationsResp = this.operationService.list("1L", "1L");

        assertEquals(operations.size(), operationsResp.size());
        Mockito.verify(this.operationRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listOperationsFromAPI() {
        List<Operation> operations = new ArrayList<>();
        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(this.operationRepository.findAll()).thenReturn(operations);

        List<Operation> operationsResp = this.operationService.list("1L");

        assertEquals(operations.size(), operationsResp.size());
        Mockito.verify(this.operationRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listOperationsNotEmptyFromAPI() {
        List<Operation> operations = new ArrayList<>();
        operations.add(operation);
        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);
        Mockito.when(this.operationRepository.findAll()).thenReturn(operations);

        List<Operation> operationsResp = this.operationService.list("1L");

        assertEquals(operations.size(), operationsResp.size());
        Mockito.verify(this.operationRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listOperationsWithPageable() {

        Pageable pageable = PageRequest.of(0, 10);

        ArrayList<Operation> listOperations = new ArrayList<>();

        listOperations.add(operation);

        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);
        Mockito.when(this.operationRepository.findAll()).thenReturn(listOperations);

        Page<Operation> operationPageResp = this.operationService
                .list("1L", "1L", pageable);

        assertEquals(1, operationPageResp.getTotalElements());
        Mockito.verify(this.operationRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    public void updateOperation() {

        Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);
        Mockito.when(operationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(operation));
        Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);

        operationDTO.setDescription("Another description");

        Operation saved = operationService.save("1L", "1L", operation);

        assertEquals(saved.getId(), operation.getId());

        Mockito.when(operationRepository.save(Mockito.any(Operation.class))).thenReturn(operation);

        Operation update = operationService.update("1L", "1L", "1L", operationDTO);

        assertEquals(update.getId(), operation.getId());
    }

    @Test
    public void deleteOperation() {

        Mockito.when(operationRepository.findById(Mockito.anyString())).thenReturn(Optional.of(operation));
        List<Interceptor> interceptors = new ArrayList<>();
        this.operationService.delete("1L", "1L", "1L");
        Mockito.verify(this.operationRepository, Mockito.times(1)).delete(operation);

    }

    @Test
    public void deleteAllFromResources() {
        Mockito.when(resourceService.find(Mockito.anyString(), Mockito.anyString())).thenReturn(res);

        this.operationService.deleteAllfromResource("1L", "1L");
        Mockito.verify(this.operationRepository, Mockito.times(0)).delete(operation);

    }

}
