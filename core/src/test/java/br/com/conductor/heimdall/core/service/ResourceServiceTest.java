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

import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.publisher.RedisRoutePublisher;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import org.assertj.core.util.Lists;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServiceTest {

     @InjectMocks
     private ResourceService resourceService;

     @Mock
     private ResourceRepository resourceRepository;

     @Mock
     private ApiService apiService;

     @Mock
     private OperationService operationService;

     @Mock
     private InterceptorService interceptorService;

     @Mock
     private RedisRoutePublisher redisRoutePublisher;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private Resource resource;

     private Api api;

     private Resource resourceDTO;

     @Before
     public void initAttributes() {

          api = new Api();
          api.setId("10L");

          resource = new Resource();
          resource.setId("1L");
          resource.setName("Local Resource");
          resource.setDescription("My Resource Description");
          resource.setApiId(api.getId());

          List<String> operations = new ArrayList<>();
          operations.add("");
          operations.add("");
          resource.setOperations(operations);

          resourceDTO = new Resource();
          resourceDTO.setName("Local Resource");
          resourceDTO.setDescription("My Resource Description");
     }

     @Test
     public void rejectNewResourceWithNameExistent() {

          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Resource already registered");

          Resource res = new Resource();
          res.setId("resId");
          res.setName("name");
          res.setApiId("10L");

          Api api = new Api();
          api.setId("10L");
          api.setResources(Sets.newSet(res.getId()));

          Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
         Mockito.when(resourceRepository.findAll()).thenReturn(Lists.list(res));

          resourceService.save("10L", res);
     }

     @Test
     public void acceptNewResourceWithPathExistentInDifferentApi() {

          Resource res = new Resource();
          res.setName("name");
          res.setDescription("description");

          Api api = new Api();
          api.setId("10L");

          Api diffApi = new Api();
          diffApi.setId("20L");

          Resource resData = new Resource();
          resData.setApiId(diffApi.getId());

          Resource newRes = new Resource();
          newRes.setId("40L");
          newRes.setName("name");
          newRes.setApiId(diffApi.getId());
          newRes.setDescription("foo description");

          Mockito.when(apiService.find("10L")).thenReturn(api);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(newRes);

          Resource resource = resourceService.save("10L", res);

          assertEquals(res.getName(), resource.getName());
          assertNotEquals(api.getId(), resource.getApiId());
     }

     @Test
     public void findResource() {

         Mockito.when(resourceRepository.findById(Mockito.anyString())).thenReturn(Optional.of(resource));

          Resource resourceResp = resourceService.find(api.getId(), "1L");
          assertEquals(resourceResp.getId(), resource.getId());
          Mockito.verify(this.resourceRepository, Mockito.times(1))
                 .findById(Mockito.anyString());
     }

     @Test
     public void listResources() {

          List<Resource> resources = new ArrayList<>();
          resources.add(resource);

          Mockito.when(this.apiService.find(api.getId())).thenReturn(api);
          Mockito.when(this.resourceRepository.findAll()).thenReturn(resources);

          List<Resource> environmentResp = this.resourceService.list(api.getId());

          assertEquals(resources.size(), environmentResp.size());
          Mockito.verify(this.resourceRepository, Mockito.times(1)).findAll();
     }

     @Test
     public void listResourcesWithPageable() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setPage(0);

          Pageable pageable = PageRequest.of(0, 10);

          ArrayList<Resource> listResources = new ArrayList<>();

          this.resource.setName("Resource Name");
          this.resource.setApiId(api.getId());

          listResources.add(resource);

          Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
          Mockito.when(this.resourceRepository.findAll()).thenReturn(listResources);

         Page<Resource> resourcePageResp = this.resourceService.list(api.getId(), pageable);

          assertEquals(1L, resourcePageResp.getTotalElements());
          Mockito.verify(this.resourceRepository, Mockito.times(1)).findAll();
     }

     @Test
     public void updateResourceSuccess() {

          Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);

          resourceDTO.setName("Another name");
          resourceDTO.setDescription("Anoter description");

          Resource saved = resourceService.save("1L", resourceDTO);

          assertEquals(saved.getId(), resource.getId());

          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);
          Mockito.when(resourceRepository.findById(Mockito.anyString())).thenReturn(Optional.of(resource));

          Resource update = resourceService.update("1L", "1L", resourceDTO);

          assertEquals(update.getId(), resource.getId());
     }

     @Test
     public void updateResourceErrorOnePerAPI() {

          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Resource already registered");

          Api resApi = new Api();
          resApi.setId("10L");

          Resource resData = new Resource();
          resData.setApiId(resApi.getId());

          resourceDTO.setDescription("Another description");
          resourceDTO.setId("10L");

         Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
         Mockito.when(resourceRepository.findById(Mockito.anyString())).thenReturn(Optional.of(resource));
         Mockito.when(resourceRepository.findAll()).thenReturn(Lists.list(resource));

          Resource update = resourceService.update("10L", resourceDTO.getId(), resourceDTO);

          assertEquals(update.getId(), resource.getId());
     }

     @Test
     public void deleteResourceFromAPI() {

          List<Resource> resources = new ArrayList<>();

          Operation operation = new Operation();
          operation.setId("1L");
          operation.setPath("path example");
          operation.setResourceId(resource.getId());
          operation.setMethod(HttpMethod.GET);

          resources.add(resource);

          Mockito.doNothing().when(operationService).deleteAllfromResource(Mockito.anyString(), Mockito.anyString());
          Mockito.doNothing().when(interceptorService).deleteAllfromResource(Mockito.anyString());

          Mockito.when(resourceRepository.findById(Mockito.anyString())).thenReturn(Optional.of(resource));
          Mockito.when(resourceRepository.findAll()).thenReturn(resources);

          resourceService.deleteAllFromApi(api.getId());
          Mockito.verify(this.resourceRepository, Mockito.times(1)).delete(Mockito.any(Resource.class));
     }

     @Test
     public void saveResource() {

          Mockito.when(apiService.find(Mockito.anyString())).thenReturn(api);
          Mockito.when(resourceRepository.save(Mockito.any(Resource.class))).thenReturn(resource);

          Resource saved = resourceService.save("1L", resource);

          assertEquals(saved.getId(), resource.getId());
     }

}
