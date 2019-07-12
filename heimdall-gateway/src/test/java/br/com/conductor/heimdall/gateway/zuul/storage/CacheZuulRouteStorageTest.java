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
package br.com.conductor.heimdall.gateway.zuul.storage;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.service.ApiService;
import br.com.conductor.heimdall.core.service.OperationService;
import br.com.conductor.heimdall.core.util.Constants;
import org.assertj.core.util.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.test.util.ReflectionTestUtils;
import org.testng.collections.Lists;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CacheZuulRouteStorageTest {

    @InjectMocks
    private CacheZuulRouteStorage storage;

    @Mock
    private ApiService apiService;

    @Mock
    private OperationService operationService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoadOneApiWithTwoResources() {
        Api api = new Api("10L",
                "foo",
                "v1",
                "fooDescription",
                "/foo",
                false,
                LocalDateTime.now(),
                new HashSet<>(),
                Status.ACTIVE,
                Sets.newHashSet(Lists.newArrayList("1L")),
                null);


        Resource resource = new Resource();
        resource.setId("88L");
        resource.setApiId(api.getId());
        resource.setOperations(new ArrayList<>());

        Operation opPost = new Operation("10L", HttpMethod.POST, "/api/foo", "POST description", resource.getId(), api.getId());
        Operation opGet = new Operation("11L", HttpMethod.GET, "/api/foo/{id}", "GET description", resource.getId(), api.getId());
        Operation opDelete = new Operation("12L", HttpMethod.DELETE, "/api/foo/{id}", "DELETE description", resource.getId(), api.getId());

        resource.addOperation(opPost.getId());
        resource.addOperation(opGet.getId());
        resource.addOperation(opDelete.getId());

        List<Api> apis = Lists.newArrayList(api);
        List<Operation> operations = Lists.newArrayList(opPost, opGet, opDelete);


        api.setResources(Sets.newHashSet(Lists.newArrayList(resource.getId())));

        Mockito.when(apiService.list()).thenReturn(apis);
        Mockito.when(operationService.list(Mockito.anyString())).thenReturn(operations);
        ReflectionTestUtils.setField(this.storage, "profile", Constants.PRODUCTION);

        List<ZuulRoute> zuulRoutes = storage.findAll();

        assertNotNull(zuulRoutes);
        assertEquals(resource.getOperations().size(), zuulRoutes.size());
    }
}
