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

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.util.Pageable;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.APP_CLIENT_ID_ALREADY_USED;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_NOT_FOUND;

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
     private DeveloperService developerService;

     @Autowired
     private AccessTokenService accessTokenService;

     @Autowired
     private AMQPCacheService amqpCacheService;

     /**
      * Finds a {@link App} by its ID.
      *
      * @param 	id						The id of the {@link App}
      * @return							The {@link App} that was found
      */
     @Transactional(readOnly = true)
     public App find(String id) {

          App app = appRepository.findOne(id);
          HeimdallException.checkThrow(app == null, GLOBAL_NOT_FOUND, "App");
          app.setAccessTokens(this.getAccessTokens(app));

          return app;
     }

     /**
      * Generates a paged list of App.
      *
      * @param 	pageable				The {@link Pageable}
      * @return							The paged {@link App} list
      */
     @Transactional(readOnly = true)
     public Page<App> list(Pageable pageable) {

          return appRepository.findAll(pageable);
     }

     /**
      * Generates a list of {@link App}.
      *
      * @return							The list of {@link App}'s
      */
     @Transactional(readOnly = true)
     public List<App> list() {

          return appRepository.findAll();
     }

     /**
      * Saves a {@link App} to the repository.
      * 
      * @param 	app					The {@link App}
      * @return							The saved {@link App}
      * @throws HeimdallException		Developer not exist, ClientId already used
      */
     @Transactional
     public App save(App app) {

          if (app.getClientId() != null) {

               HeimdallException.checkThrow(appRepository.findByClientId(app.getClientId()) != null, APP_CLIENT_ID_ALREADY_USED);
          } else {
               RandomString randomString = new RandomString(12);
               String token = randomString.nextString();

               while (appRepository.findByClientId(token) != null) {
                    token = randomString.nextString();
               }

               app.setClientId(token);
          }

          app.setCreationDate(LocalDateTime.now());
          app.setClientId(app.getClientId().trim());

          developerService.find(app.getDeveloperId());

          amqpCacheService.dispatchClean();

          return appRepository.save(app);

     }

     /**
      * Updates a {@link App} by its ID.
      *
      * @param 	id						The ID of the {@link App}
      * @param 	appPersist					{@link App}
      * @return							The updated {@link App}
      * @throws HeimdallException		Resource not found
      */
     public App update(String id, App appPersist) {

          App app = this.find(id);

          updateTokensPlansByApp(id, appPersist.getPlans());
          
          app.setAccessTokens(this.getAccessTokens(app));
          app = GenericConverter.mapper(appPersist, app);
          app = appRepository.save(app);
          
          amqpCacheService.dispatchClean();
          
          return app;
     }

     public App update(App app) {
          return this.update(app.getId(), app);
     }

     /**
      * Updates app's access tokens.
      * This is used for removing the access token to plan association, only if an app removes one of it's plans. 
      * 
      * @param appId The ID of the {@link App}
      * @param plansIds List of {@link Plan}'s IDs 
      */
     private void updateTokensPlansByApp(String appId, Set<String> plansIds) {
          List<AccessToken> accessTokenList = accessTokenService.findByAppId(appId);

          if (Objects.nonNull(accessTokenList)) {
               accessTokenList.forEach(accessToken -> {
                    if (Objects.nonNull(accessToken.getPlans()) && !accessToken.getPlans().isEmpty()) {
                         List<String> planList = accessToken.getPlans().stream().filter(plansIds::contains).collect(Collectors.toList());
                         accessToken.setPlans(planList);
                         accessTokenService.update(accessToken);
                    }
               });
          }
     }
     
     /**
      * Deletes a {@link App} by its ID.
      * 
      * @param  id						The ID of the {@link App}
      * @throws HeimdallException		Resource not found
      */
     public void delete(String id) {

          App app = this.find(id);

          amqpCacheService.dispatchClean();

          appRepository.delete(app);
     }

     private Set<String> getAccessTokens(App app) {
          return accessTokenService.findByAppId(app.getId()).stream()
                  .map(AccessToken::getId)
                  .collect(Collectors.toSet())
                  ;
     }

}
