
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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.ACCESS_TOKEN_ALREADY_EXISTS;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.ACCESS_TOKEN_NOT_DEFINED;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.APP_NOT_EXIST;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.SOME_PLAN_NOT_PRESENT_IN_APP;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.integration.AccessTokenDTO;
import br.com.conductor.heimdall.core.dto.page.AccessTokenPage;
import br.com.conductor.heimdall.core.dto.persist.AccessTokenPersist;
import br.com.conductor.heimdall.core.dto.request.AccessTokenRequest;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.util.Pageable;
import net.bytebuddy.utility.RandomString;

/**
 * This class provides methods to create, read, update and delete the {@link AccessToken} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class AccessTokenService {

     @Autowired
     private AccessTokenRepository accessTokenRepository;

     @Autowired
     private AppRepository appRespository;

     @Autowired
     private AppService appService;

     @Autowired
     private PlanRepository planRepository;

     @Autowired
     private AMQPCacheService amqpCacheService;

     /**
      * Looks for a {@link AccessToken} based on it's.
      *
      * @param 	id 						The id of the {@link AccessToken}
      * @return  						The {@link AccessToken} found
      */
     @Transactional(readOnly = true)
     public AccessToken find(Long id) {

          AccessToken accessToken = accessTokenRepository.findOne(id);
          HeimdallException.checkThrow(accessToken == null, GLOBAL_RESOURCE_NOT_FOUND);

          return accessToken;
     }

     /**
      * Returns a paged list of all {@link AccessToken} from a request.
      *
      * @param 	accessTokenRequest 		{@link AccessTokenRequest} The request for {@link AccessToken}
      * @param 	pageableDTO 			{@link PageableDTO} The pageable DTO
      * @return 						The paged {@link AccessToken} list as a {@link AccessTokenPage} object
      */
     @Transactional(readOnly = true)
     public AccessTokenPage list(AccessTokenRequest accessTokenRequest, PageableDTO pageableDTO) {

          AccessToken accessToken = GenericConverter.mapper(accessTokenRequest, AccessToken.class);

          Example<AccessToken> example = Example.of(accessToken, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<AccessToken> page = accessTokenRepository.findAll(example, pageable);

          AccessTokenPage accessTokenPage = new AccessTokenPage(PageDTO.build(page));

          return accessTokenPage;
     }

     /**
      * Returns a list of all {@link AccessToken} from a request
      *
      * @param 	accessTokenRequest 		{@link AccessTokenRequest} The request for {@link AccessToken}
      * @return 						The list of {@link AccessToken}
      */
     @Transactional(readOnly = true)
     public List<AccessToken> list(AccessTokenRequest accessTokenRequest) {

          AccessToken accessToken = GenericConverter.mapper(accessTokenRequest, AccessToken.class);

          Example<AccessToken> example = Example.of(accessToken, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          List<AccessToken> accessTokens = accessTokenRepository.findAll(example);

          return accessTokens;
     }

     /**
      * Saves a new {@link AccessToken} for a {@link App}. If the {@link AccessToken} does not
      * have a token it generates a new token for it.
      *
      * @param 	accessTokenPersist 		{@link AccessTokenPersist}
      * @return 						The {@link AccessToken} that was saved to the repository
      */
     @Transactional
     public AccessToken save(AccessTokenPersist accessTokenPersist) {

          AccessToken accessToken = GenericConverter.mapper(accessTokenPersist, AccessToken.class);

          App appRecover = appRespository.findOne(accessTokenPersist.getApp().getId());
          HeimdallException.checkThrow(appRecover == null, APP_NOT_EXIST);
          HeimdallException.checkThrow(!verifyIfPlansContainsInApp(appRecover, accessTokenPersist.getPlans()), SOME_PLAN_NOT_PRESENT_IN_APP);

          AccessToken existAccessToken;
          if (accessToken.getCode() != null) {

               existAccessToken = accessTokenRepository.findByCode(accessToken.getCode());
               HeimdallException.checkThrow(existAccessToken != null, ACCESS_TOKEN_ALREADY_EXISTS);
          } else {

               RandomString randomString = new RandomString(12);
               String token = randomString.nextString();

               while (accessTokenRepository.findByCode(token) != null) {
            	   token = randomString.nextString();
               }

               accessToken.setCode(token);
          }

          accessToken = accessTokenRepository.save(accessToken);

          return accessToken;
     }

     /**
      * Updates a {@link AccessToken} by its ID.
      *
      * @param 	id 						The ID of the {@link AccessToken} to be updated
      * @param 	accessTokenPersist 		{@link AccessTokenPersist} The request for {@link AccessToken}
      * @return 						The {@link AccessToken} updated
      */
     @Transactional
     public AccessToken update(Long id, AccessTokenPersist accessTokenPersist) {

          AccessToken accessToken = accessTokenRepository.findOne(id);
          HeimdallException.checkThrow(accessToken == null, GLOBAL_RESOURCE_NOT_FOUND);

          App appRecover = appRespository.findOne(accessTokenPersist.getApp().getId());
          HeimdallException.checkThrow(appRecover == null, APP_NOT_EXIST);
          HeimdallException.checkThrow(!verifyIfPlansContainsInApp(appRecover, accessTokenPersist.getPlans()), SOME_PLAN_NOT_PRESENT_IN_APP);

          accessToken = GenericConverter.mapper(accessTokenPersist, accessToken);
          accessToken = accessTokenRepository.save(accessToken);

          amqpCacheService.dispatchClean();

          return accessToken;
     }

     /**
      * Deletes a {@link AccessToken} by its ID.
      *
      * @param 	id 						The ID of the {@link AccessToken} to be deleted
      */
     @Transactional
     public void delete(Long id) {

          AccessToken accessToken = accessTokenRepository.findOne(id);
          HeimdallException.checkThrow(accessToken == null, GLOBAL_RESOURCE_NOT_FOUND);

          amqpCacheService.dispatchClean();

          accessTokenRepository.delete(accessToken);
     }

     /**
      * Saves a new {@link AccessToken} for a {@link App}.
      *
      * @param reqBody					The {@link AccessTokenDTO}
      * @return							The {@link AccessToken} saved
      */
     @Transactional
     public AccessToken save(AccessTokenDTO reqBody) {

          AccessToken accessToken = GenericConverter.mapper(reqBody, AccessToken.class);
          HeimdallException.checkThrow(accessToken.getCode() == null, ACCESS_TOKEN_NOT_DEFINED);

          App app = appService.save(reqBody.getApp());

          AccessToken existAccessToken;

          existAccessToken = accessTokenRepository.findByCode(accessToken.getCode());
          HeimdallException.checkThrow(existAccessToken != null, ACCESS_TOKEN_ALREADY_EXISTS);

          accessToken.setApp(app);

          Plan plan = planRepository.findOne(1L);
          if (plan != null) {

               accessToken.setPlans(Lists.newArrayList(plan));
//               accessToken.setPlans(Arrays.asList(plan));
          }

          accessToken = accessTokenRepository.save(accessToken);

          return accessToken;
     }

     /**
      * Verify if all plans informed contains in {@link List}<{@link Plan}> of the {@link App}
      *
      * @param app The {@link App}
      * @param plansId {@link List}<{@link ReferenceIdDTO}> plansIds
      * @return True if contains all, false otherwise
      */
     private boolean verifyIfPlansContainsInApp(App app, List<ReferenceIdDTO> plansId) {
          final List<Long> plansSelected = plansId.stream().map(ReferenceIdDTO::getId).collect(Collectors.toList());
          return plansId.size() == app.getPlans().stream()
                  .filter(plan -> Collections.frequency(plansSelected, plan.getId()) == 1)
                  .count();
     }

}
