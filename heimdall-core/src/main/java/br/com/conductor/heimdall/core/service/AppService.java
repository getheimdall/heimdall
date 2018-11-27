
package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;

import java.util.List;
import java.util.Objects;

import br.com.conductor.heimdall.core.converter.AppPersistMap;
import br.com.conductor.heimdall.core.dto.persist.AppPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import br.com.conductor.heimdall.core.converter.AppMap;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.AppDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.integration.AppCallbackDTO;
import br.com.conductor.heimdall.core.dto.page.AppPage;
import br.com.conductor.heimdall.core.dto.request.AppRequestDTO;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.repository.DeveloperRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.util.Pageable;
import br.com.twsoftware.alfred.object.Objeto;
import net.bytebuddy.utility.RandomString;

/**
 * This class provides methods to create, read, update and delete the {@link App} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class AppService {

     @Autowired
     private AppRepository appRepository;

     @Autowired
     private DeveloperRepository devRepository;

     @Autowired
     private PlanRepository planRepository;

     @Autowired
     private AccessTokenRepository accessTokenRepository;

     @Autowired
     private AMQPCacheService amqpCacheService;

     /**
      * Finds a {@link App} by its ID.
      *
      * @param 	id						The id of the {@link App}
      * @return							The {@link App} that was found
      */
     @Transactional(readOnly = true)
     public App find(Long id) {

          App app = appRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(app), GLOBAL_RESOURCE_NOT_FOUND);
          app.setAccessTokens(accessTokenRepository.findByAppId(app.getId()));

          return app;
     }

     /**
      * Generates a paged list of App.
      *
      * @param 	appDTO					The {@link AppDTO}
      * @param 	pageableDTO				The {@link PageableDTO}
      * @return							The paged {@link App} list as a {@link AppPage} object
      */
     @Transactional(readOnly = true)
     public AppPage list(AppRequestDTO appDTO, PageableDTO pageableDTO) {

          App app = GenericConverter.mapper(appDTO, App.class);

          Example<App> example = Example.of(app, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<App> page = appRepository.findAll(example, pageable);

          AppPage appPage = new AppPage(PageDTO.build(page));

          return appPage;
     }

     /**
      * Generates a list of {@link App}.
      *
      * @param 	appDTO					The {@link AppDTO}
      * @return							The list of {@link App}'s
      */
     @Transactional(readOnly = true)
     public List<App> list(AppRequestDTO appDTO) {

          App app = GenericConverter.mapper(appDTO, App.class);

          Example<App> example = Example.of(app, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          List<App> apps = appRepository.findAll(example);

          return apps;
     }

     /**
      * Saves a {@link App} to the repository.
      * 
      * @param 	appDTO					The {@link AppPersist}
      * @return							The saved {@link App}
      * @throws HeimdallException		Developer not exist, ClientId already used
      */
     public App save(AppPersist appDTO) {

          if (Objeto.notBlank(appDTO.getClientId())) {
               App app = appRepository.findByClientId(appDTO.getClientId());
               HeimdallException.checkThrow(Objects.nonNull(app), CLIENT_ID_ALREADY);
          } else {
               RandomString randomString = new RandomString(12);
               String token = randomString.nextString();

               while (appRepository.findByClientId(token) != null) {
                    token = randomString.nextString();
               }

               appDTO.setClientId(token);
          }

          App app = GenericConverter.mapperWithMapping(appDTO, App.class, new AppPersistMap());

          Developer dev = devRepository.findOne(app.getDeveloper().getId());
          HeimdallException.checkThrow(isBlank(dev), DEVELOPER_NOT_EXIST);

          amqpCacheService.dispatchClean();

          return appRepository.save(app);

     }

     /**
      * Updates a {@link App} by its ID.
      *
      * @param 	id						The ID of the {@link App}
      * @param 	appDTO					{@link AppDTO}
      * @return							The updated {@link App}
      * @throws HeimdallException		Resource not found
      */
     public App update(Long id, AppDTO appDTO) {

          App app = appRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(app), GLOBAL_RESOURCE_NOT_FOUND);
          
          app.setAccessTokens(accessTokenRepository.findByAppId(app.getId()));
          app = GenericConverter.mapperWithMapping(appDTO, app, new AppMap());
          app = appRepository.save(app);
          
          amqpCacheService.dispatchClean();
          
          return app;
     }
     
     /**
      * Deletes a {@link App} by its ID.
      * 
      * @param  id						The ID of the {@link App}
      * @throws HeimdallException		Resource not found
      */
     public void delete(Long id) {

          App app = appRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(app), GLOBAL_RESOURCE_NOT_FOUND);

          amqpCacheService.dispatchClean();

          appRepository.delete(app);
     }

     /**
      * Saves a {@link App}.
      *
      * @param  reqBody					{@link AppCallbackDTO}
      * @return							The {@link App} saved
      * @throws HeimdallException		Developer not exist
      */
     @Transactional
     public App save(AppCallbackDTO reqBody) {
          App app = appRepository.findByClientId(reqBody.getCode());

          Developer dev = devRepository.findByEmail(reqBody.getDeveloper());
          HeimdallException.checkThrow(isBlank(dev), DEVELOPER_NOT_EXIST);

          if (isBlank(app)) {

               app = new App();

          } else {

               List<Plan> plans = appRepository.findPlansByApp(app.getId());
               app.setPlans(plans);
          }

          app.setClientId(reqBody.getCode());
          app.setDeveloper(dev);
          app.setDescription(reqBody.getDescription());
          app.setName(reqBody.getName());

          if (app.getPlans() != null && app.getPlans().isEmpty()) {

               Plan plan = planRepository.findOne(1L);
               if (Objeto.notBlank(plan)) {
                    app.setPlans(Lists.newArrayList(plan));
               }
          }

          app = appRepository.save(app);

          amqpCacheService.dispatchClean();

          return app;
     }

}
