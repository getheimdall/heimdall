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

import br.com.conductor.heimdall.core.dto.DeveloperDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.DeveloperPage;
import br.com.conductor.heimdall.core.dto.request.DeveloperLogin;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.repository.DeveloperRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import org.apache.commons.lang.RandomStringUtils;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://github.com/cassioesp" target="_blank">Cássio Espíndola/a>
 */
@RunWith(MockitoJUnitRunner.class)
public class DeveloperServiceTest {

     @InjectMocks
     private DeveloperService developerService;

     @Mock
     private DeveloperRepository developerRepository;

     @Mock
     private AMQPRouteService amqpRoute;

     @Mock
     private AMQPCacheService amqpCacheService;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     private DeveloperDTO developerDTO;

     private Developer developer;

     @Before
     public void initAttributes() {

          developer = new Developer();
          developer.setId(1L);
          developer.setName("My Developer");
          developer.setEmail("developer@conductor.com.br");
          developer.setCreationDate(LocalDateTime.now());
          developer.setPassword(RandomStringUtils.randomAlphabetic(10));
          developer.setStatus(Status.ACTIVE);

          List<App> apps = new ArrayList<>();
          App myTestApp = new App();
          App myTestApp2 = new App();
          apps.add(myTestApp);
          apps.add(myTestApp2);
          developer.setApps(apps);

          developerDTO = new DeveloperDTO();
          developerDTO.setName("My Developer DTO");
          developerDTO.setEmail("developer@conductor.com.br");
          developerDTO.setPassword(RandomStringUtils.randomAlphabetic(10));
          developerDTO.setStatus(Status.ACTIVE);
     }

     @Test
     public void saveDefaultDeveloperSuccess() {

          Developer developer = new Developer();
          developer.setId(1L);
          developer.setName("My Developer");
          developer.setEmail("developer@conductor.com.br");
          developer.setCreationDate(LocalDateTime.now());
          developer.setStatus(Status.ACTIVE);
          List<App> apps = new ArrayList<>();
          App myTestApp = new App();
          App myTestApp2 = new App();
          apps.add(myTestApp);
          apps.add(myTestApp2);
          developer.setApps(apps);

          Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);

          Developer saved = developerService.save(developerDTO);

          assertEquals(saved.getId(), developer.getId());
     }

     @Test
     public void findDeveloper() {

          Mockito.when(this.developerRepository.findOne(Mockito.any(Long.class))).thenReturn(developer);
          Developer developerResp = developerService.find(1L);
          assertEquals(developerResp.getId(), developer.getId());
          Mockito.verify(this.developerRepository, Mockito.times(1)).findOne(Mockito.any(Long.class));
     }

     @Test
     public void listDevelopers() {

          developer.setName("Developer Name");

          List<Developer> developers = new ArrayList<>();
          developers.add(developer);

          Mockito.when(this.developerRepository.findAll(Mockito.any(Example.class))).thenReturn(developers);

          List<Developer> developerResp = this.developerService.list(this.developerDTO);

          assertEquals(developers.size(), developerResp.size());
          Mockito.verify(this.developerRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listDevelopersWithPageable() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setOffset(0);

          ArrayList<Developer> listDevelopers = new ArrayList<>();

          listDevelopers.add(developer);

          Page<Developer> page = new PageImpl<>(listDevelopers);

          Mockito.when(this.developerRepository
                                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                 .thenReturn(page);

          DeveloperPage developerPageResp = this.developerService.list(this.developerDTO, pageableDTO);

          assertEquals(1L, developerPageResp.getTotalElements());
          Mockito.verify(this.developerRepository, Mockito.times(1))
                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
     }

     @Test
     public void updateDeveloper() {

          Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);

          Developer saved = developerService.save(developerDTO);

          Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);
          Mockito.when(developerRepository.findOne(Mockito.anyLong())).thenReturn(developer);

          Developer update = developerService.update(1L, developerDTO);

          assertEquals(update.getId(), developer.getId());
     }

     @Test
     public void deleteDeveloper() {

          Mockito.when(developerRepository.findOne(Mockito.anyLong())).thenReturn(developer);
          this.developerService.delete(1L);
          Mockito.verify(this.developerRepository, Mockito.times(1)).delete(developer);
     }

     @Test
     public void loginDeveloper() {

          Mockito.when(developerRepository.findByEmailAndPassword(Mockito.anyString(), Mockito.anyString()))
                 .thenReturn(developer);
          DeveloperLogin developerLogin = new DeveloperLogin();
          developerLogin.setEmail(Mockito.anyString());
          developerLogin.setPassword(Mockito.anyString());
          this.developerService.login(developerLogin);
          Mockito.verify(this.developerRepository, Mockito.times(1))
                 .findByEmailAndPassword(Mockito.anyString(), Mockito.anyString());
     }

}
