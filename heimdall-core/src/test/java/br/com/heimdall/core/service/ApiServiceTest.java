package br.com.heimdall.core.service;

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

import br.com.heimdall.core.dto.ApiDTO;
import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.ReferenceIdDTO;
import br.com.heimdall.core.dto.page.ApiPage;
import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.entity.Environment;
import br.com.heimdall.core.entity.Plan;
import br.com.heimdall.core.enums.Status;
import br.com.heimdall.core.exception.BadRequestException;
import br.com.heimdall.core.repository.ApiRepository;
import br.com.heimdall.core.repository.ResourceRepository;
import br.com.heimdall.core.service.amqp.AMQPRouteService;
import io.swagger.models.Swagger;
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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
     private ResourceRepository resourceRepository;

     @Mock
     private EnvironmentService environmentService;

     @Mock
     private ResourceService resourceService;

     @Mock
     private SwaggerService swaggerService;

     @Mock
     private MiddlewareService middlewareService;

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

     @Test
     public void listApiWithoutPageableTest() {

          List<Api> apis = new ArrayList<>();
          apis.add(api);

          Mockito.when(this.apiRepository.findAll(Mockito.any(Example.class))).thenReturn(apis);

          List<Api> listApiResp = apiService.list(apiDTO);

          assertEquals(apis.size(), listApiResp.size());
          Mockito.verify(this.apiRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listApiWithPageableTest() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setOffset(0);

          List<Api> apis = new ArrayList<>();
          apis.add(api);

          Page<Api> page = new PageImpl<>(apis);

          Mockito.when(this.apiRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                 .thenReturn(page);

          ApiPage apiPageResp = apiService.list(apiDTO, pageableDTO);

          Assert.assertEquals(1L, apiPageResp.getTotalElements());
          Mockito.verify(this.apiRepository, Mockito.times(1))
                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
     }

     @Test
     public void findApiTest() {

          Mockito.when(this.apiRepository.findOne(Mockito.any(Long.class))).thenReturn(api);
          Api apiResp = apiService.find(1L);
          assertEquals(apiResp.getId(), api.getId());
          Mockito.verify(this.apiRepository, Mockito.times(1)).findOne(Mockito.any(Long.class));
     }

     @Test
     public void deleteApiTest() {

          Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
          this.apiService.delete(1L);
          Mockito.verify(this.apiRepository, Mockito.times(1)).delete(api);
     }

     @Test
     public void findSwaggerByApiTest() {

          Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
          Mockito.when(resourceRepository.findAll(Mockito.any(Example.class))).thenReturn(Mockito.anyList());
          Mockito.when(swaggerService.exportApiToSwaggerJSON(api)).thenReturn(new Swagger());
          Swagger result = this.apiService.findSwaggerByApi(1L);
          Mockito.verify(swaggerService, Mockito.times(1)).exportApiToSwaggerJSON(api);
          assertNotNull(result);
     }

     @Test
     public void updateBySwaggerTest() throws IOException {

          Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
          Mockito.when(swaggerService.importApiFromSwaggerJSON(api, "adsqwe", false)).thenReturn(api);

          apiService.updateBySwagger(1L, "asd", false);
          Mockito.verify(apiRepository, Mockito.times(1)).save(Mockito.any(Api.class));
     }

     @Test
     public void updateBySwaggerApiNullTest() throws IOException {

          Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
          Mockito.when(swaggerService.importApiFromSwaggerJSON(api, "adsqwe", false))
                 .thenThrow(IOException.class);

          apiService.updateBySwagger(1L, "adsqwe", false);
          Mockito.verify(apiRepository, Mockito.times(1)).save(Mockito.any(Api.class));
     }

     @Test
     public void plansByApiTest() {
          List<Plan> plans = new ArrayList<>();
          plans.add(new Plan());
          plans.add(new Plan());
          api.setPlans(plans);
          Mockito.when(apiRepository.findOne(Mockito.anyLong())).thenReturn(api);
          List<Plan> plansResp = apiService.plansByApi(1L);
          assertNotNull(plansResp);
     }
}
