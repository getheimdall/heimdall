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
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.EnvironmentRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.publisher.RedisRoutePublisher;
import io.swagger.models.Swagger;
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

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {

    @InjectMocks
    private ApiService apiService;

    @Mock
    private PlanService planService;

    @Mock
    private EnvironmentService environmentService;

    @Mock
    private InterceptorService interceptorService;

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    private SwaggerService swaggerService;

    @Mock
    private RedisRoutePublisher amqpRoute;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Api apiPersist;

    private Api api;

    @Before
    public void initAttributes() {

        api = new Api();
        api.setId("1L");
        api.setBasePath("/test");

        apiPersist = new Api();
        apiPersist.setName("test");
        apiPersist.setBasePath("/test");
        apiPersist.setDescription("test");
        apiPersist.setVersion("1.0.0");
        apiPersist.setStatus(Status.ACTIVE);
    }

    @Test
    public void saveTestWithSuccess() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8082");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiPersist);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithEnvironmentNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);

        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(null);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiPersist);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithInboundEnvironmentsNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();

        Environment e3 = new Environment();

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiPersist);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithInboundEnvironmentsEmpty() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Apis can't have environments with the same inbound url");

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("");

        Environment e3 = new Environment();
        e3.setInboundURL("");

        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiPersist);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestExpectedException() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Apis can't have environments with the same inbound url");

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8081");

        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiPersist);
    }

    @Test
    public void updateTestSuccess() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8082");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api update = apiService.update("1L", apiPersist);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestEnvironmentsNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(null);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api update = apiService.update("1L", apiPersist);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestInboundsEnvironmentNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        Environment e3 = new Environment();

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api update = apiService.update("1L", apiPersist);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestInboundsEnvironmentEmpty() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Apis can't have environments with the same inbound url");

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("");

        Environment e3 = new Environment();
        e3.setInboundURL("");

        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api update = apiService.update("1L", apiPersist);

    }

    @Test
    public void updateTestExpectedException() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Apis can't have environments with the same inbound url");

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8080");

        Environment e3 = new Environment();
        e3.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find("1L")).thenReturn(e1);
        Mockito.when(environmentService.find("2L")).thenReturn(e2);
        Mockito.when(environmentService.find("3L")).thenReturn(e3);

        Set<String> environmentsDTO = new HashSet<>();
        environmentsDTO.add("1L");
        environmentsDTO.add("2L");
        environmentsDTO.add("3L");

        apiPersist.setEnvironments(environmentsDTO);

        Api update = apiService.update("1L", apiPersist);
    }

    @Test
    public void listApiWithoutPageableTest() {

        List<Api> apis = new ArrayList<>();
        apis.add(api);

        Mockito.when(this.apiRepository.findAll()).thenReturn(apis);

        List<Api> listApiResp = apiService.list();

        assertEquals(apis.size(), listApiResp.size());
        Mockito.verify(this.apiRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listApiWithPageableTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Api> apis = new ArrayList<>();
        apis.add(api);

        Mockito.when(this.apiRepository.findAll()).thenReturn(apis);

        Page<Api> apiPageResp = apiService.list(pageable);

        assertEquals(1L, apiPageResp.getTotalElements());
        Mockito.verify(this.apiRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findApiTest() {

        Mockito.when(this.apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Api apiResp = apiService.find("1L");
        assertEquals(apiResp.getId(), api.getId());
        Mockito.verify(this.apiRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void deleteApiTest() {

        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        this.apiService.delete("1L");
        Mockito.verify(this.apiRepository, Mockito.times(1)).delete(api);
    }

    @Test
    public void findSwaggerByApiTest() {

        api.setResources(Collections.emptySet());
        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(swaggerService.exportApiToSwaggerJSON(api)).thenReturn(new Swagger());
        Swagger result = this.apiService.findSwaggerByApi("1L");
        Mockito.verify(swaggerService, Mockito.times(1)).exportApiToSwaggerJSON(api);
        assertNotNull(result);
    }

    @Test
    public void updateBySwaggerTest() throws IOException {

        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(swaggerService.importApiFromSwaggerJSON(api, "asd", false)).thenReturn(api);

        apiService.updateBySwagger("1L", "asd", false);
        Mockito.verify(apiRepository, Mockito.times(1)).save(Mockito.any(Api.class));
    }

    @Test
    public void updateBySwaggerApiNullTest() throws IOException {

        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Mockito.when(swaggerService.importApiFromSwaggerJSON(api, "adsqwe", false))
                .thenThrow(IOException.class);

        apiService.updateBySwagger("1L", "adsqwe", false);
        Mockito.verify(apiRepository, Mockito.times(1)).save(Mockito.any(Api.class));
    }

    @Test
    public void plansByApiTest() {
        Plan plan = new Plan();
        plan.setId("10");

        Set<String> plans = new HashSet<>();
        plans.add(plan.getId());
        api.setPlans(plans);
        Mockito.when(apiRepository.findById(Mockito.anyString())).thenReturn(Optional.of(api));
        Set<Plan> plansResp = apiService.plansByApi("1L");
        assertNotNull(plansResp);
    }
}
