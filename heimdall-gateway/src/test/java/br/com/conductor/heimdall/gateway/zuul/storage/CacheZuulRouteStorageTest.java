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
package br.com.conductor.heimdall.gateway.zuul.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.util.Constants;

@RunWith(MockitoJUnitRunner.class)
public class CacheZuulRouteStorageTest {

     @InjectMocks
     private CacheZuulRouteStorage storage;
     
     @Mock
     private ApiRepository repository;

     @Mock
     private ResourceRepository resourceRepository;
     
     @Before
     public void setup() {
          MockitoAnnotations.initMocks(this);
     }
     
     @Test
     public void testLoadOneApiWithTwoResources() {
          List<Api> apis = new LinkedList<>();
          List<Environment> environments = Lists.newArrayList();
          Environment environment = new Environment();
          environment.setInboundURL("dns.production.com.br");
          environment.setOutboundURL("dns.production.com.br");
          Api api = new Api(10L, "foo", "v1", "fooDescription", "/foo", LocalDateTime.now(), new LinkedList<Resource>(), Status.ACTIVE, null, null, environments, null, null);
          
          List<Resource> res = new LinkedList<>();
          
          Resource resource = new Resource();
          resource.setId(88L);
          resource.setApi(api);
          resource.setOperations(new ArrayList<>());
          
          Operation opPost = new Operation(10L, HttpMethod.POST, "/api/foo", "POST description", resource);
          Operation opGet = new Operation(10L, HttpMethod.GET, "/api/foo/{id}", "GET description", resource);
          Operation opDelete = new Operation(10L, HttpMethod.DELETE, "/api/foo/{id}", "DELETE description", resource);
          resource.getOperations().addAll(Arrays.asList(opDelete, opGet, opPost));
          
          res.add(resource);
          api.setResources(res);
          apis.add(api);
          
          Mockito.when(repository.findByStatus(Status.ACTIVE)).thenReturn(apis);
          Mockito.when(resourceRepository.findByApiId(Mockito.anyLong())).thenReturn(res);
          ReflectionTestUtils.setField(this.storage, "profile", Constants.PRODUCTION);
          
          List<ZuulRoute> zuulRoutes = storage.findAll();
          
          assertNotNull(zuulRoutes);
          assertEquals(resource.getOperations().size(), zuulRoutes.size());
     }
}
