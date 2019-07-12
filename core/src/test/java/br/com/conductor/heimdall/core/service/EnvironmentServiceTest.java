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

import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.EnvironmentRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://github.com/cassioesp" target="_blank">Cássio Espíndola/a>
 */
@RunWith(MockitoJUnitRunner.class)
public class EnvironmentServiceTest {

    @InjectMocks
    private EnvironmentService environmentService;

    @Mock
    private EnvironmentRepository environmentRepository;

    @Mock
    private ApiService apiService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Environment environmentDTO;

    private Environment environment;

    @Before
    public void initAttributes() {

        environment = new Environment();
        environment.setId("1");
        environment.setName("Local Environment");
        environment.setDescription("My environment Description");
        environment.setInboundURL("http://10.0.0.1:9090");
        environment.setOutboundURL("http://10.0.0.1:9091");
        environment.setCreationDate(LocalDateTime.now());
        environment.setStatus(Status.ACTIVE);
        Map<String, String> variables = new HashMap<>();
        variables.put("hostPier", "http://localhost:8082/v2/");
        variables.put("hostPierAccessToken", "pier");
        environment.setVariables(variables);

        environmentDTO = new Environment();
        environmentDTO.setName("Local Environment");
        environmentDTO.setDescription("My environment Description");
        environmentDTO.setInboundURL("http://127.0.0.1:9090");
        environmentDTO.setOutboundURL("http://127.0.0.1:9091");
        environmentDTO.setStatus(Status.ACTIVE);
        environmentDTO.setVariables(variables);
    }

    @Test
    public void saveEnvironmentSuccess() {

        Mockito.when(environmentRepository.save(Mockito.any(Environment.class))).thenReturn(environment);

        Environment saved = environmentService.save(environmentDTO);

        assertEquals(saved.getId(), environment.getId());
    }

    @Test
    public void saveEnviromentMalFormedInboundURL() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage(
                "Environment inbound URL has to follow the pattern http[s]://host.domain[:port] or www.host.domain[:port]");

        environmentDTO.setInboundURL("http://malformedURL:9090");
        Environment saved = environmentService.save(environmentDTO);
    }

    @Test
    public void findEnvironment() {

        Mockito.when(this.environmentRepository.findById(Mockito.anyString())).thenReturn(Optional.of(environment));
        Environment environmentResp = environmentService.find("1");
        assertEquals(environmentResp.getId(), environment.getId());
        Mockito.verify(this.environmentRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void listEnvironments() {

        List<Environment> environments = new ArrayList<>();
        environments.add(environment);

        Mockito.when(this.environmentRepository.findAll())
                .thenReturn(environments);

        List<Environment> environmentResp = this.environmentService.list();

        assertEquals(environments.size(), environmentResp.size());
        Mockito.verify(this.environmentRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listEnvironmentsWithPageable() {

        Pageable pageable = PageRequest.of(0, 10);
        ArrayList<Environment> environments = new ArrayList<>();

        environments.add(environment);

        Page<Environment> page = new PageImpl<>(environments);

        Mockito.when(this.environmentRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<Environment> environmentPageResp = this.environmentService
                .list(pageable);

        assertEquals(1L, environmentPageResp.getTotalElements());
        Mockito.verify(this.environmentRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    public void updateEnvironmentSuccess() {

        Mockito.when(environmentRepository.save(Mockito.any(Environment.class))).thenReturn(environment);

        environmentDTO.setStatus(Status.INACTIVE);
        environmentDTO.setInboundURL("http://127.0.0.1:9092");

        Environment saved = environmentService.save(environmentDTO);

        assertEquals(saved.getId(), environment.getId());

        Mockito.when(environmentRepository.save(Mockito.any(Environment.class))).thenReturn(environment);
        Mockito.when(environmentRepository.findById(Mockito.anyString())).thenReturn(Optional.of(environment));

        Environment update = environmentService.update("1", environmentDTO);

        assertEquals(update.getId(), environment.getId());
    }

    @Test
    public void deleteEnviroment() {

        Mockito.when(environmentRepository.findById(Mockito.anyString())).thenReturn(Optional.of(environment));
        Mockito.when(apiService.list()).thenReturn(new ArrayList<>());
        this.environmentService.delete("1");
        Mockito.verify(this.environmentRepository, Mockito.times(1)).delete(environment);
    }
}
