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

import br.com.conductor.heimdall.core.dto.ApiDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiServiceTest {


    @InjectMocks
    private ApiService apiService;

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private EnvironmentService environmentService;

    @Mock
    private AMQPRouteService amqpRoute;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ApiDTO apiDTO;

    private Api api;

    @Before
    public void initAttributes() {
        api = new Api();
        api.setId(1L);
        api.setBasePath("/test");

        apiDTO = new ApiDTO();
        apiDTO.setName("test");
        apiDTO.setBasePath("/test");
        apiDTO.setDescription("test");
        apiDTO.setVersion("1.0.0");
        apiDTO.setStatus(Status.ACTIVE);
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
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithEnvironmentNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(null);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithInboundEnvironmentsNull() {
        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();

        Environment e3 = new Environment();

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

        assertEquals(saved.getId(), api.getId());
    }

    @Test
    public void saveTestWithInboundEnvironmentsEmpty() {
        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("");

        Environment e3 = new Environment();
        e3.setInboundURL("");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);

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

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api saved = apiService.save(apiDTO);
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
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestEnvironmentsNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("http://localhost:8081");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(null);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestInboundsEnvironmentNull() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();

        Environment e3 = new Environment();

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
    }

    @Test
    public void updateTestInboundsEnvironmentEmpty() {

        Environment e1 = new Environment();
        e1.setInboundURL("http://localhost:8080");

        Environment e2 = new Environment();
        e2.setInboundURL("");

        Environment e3 = new Environment();
        e3.setInboundURL("");

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);

        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);

        assertEquals(update.getId(), api.getId());
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

        Mockito.when(apiRepository.save(Mockito.any(Api.class))).thenReturn(api);
        Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
        Mockito.when(apiRepository.findByBasePath(Mockito.anyString())).thenReturn(api);
        Mockito.when(environmentService.find(1L)).thenReturn(e1);
        Mockito.when(environmentService.find(2L)).thenReturn(e2);
        Mockito.when(environmentService.find(3L)).thenReturn(e3);


        List<ReferenceIdDTO> environmentsDTO = new ArrayList<>();
        environmentsDTO.add(new ReferenceIdDTO(1L));
        environmentsDTO.add(new ReferenceIdDTO(2L));
        environmentsDTO.add(new ReferenceIdDTO(3L));

        apiDTO.setEnvironments(environmentsDTO);

        Api update = apiService.update(1L, apiDTO);
    }
}
