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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.PLAN_ATTACHED_TO_APPS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.PlanDTO;
import br.com.conductor.heimdall.core.dto.page.PlanPage;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * This class provides methods to create, read, update and delete a {@link Plan} resource.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Service
public class PlanService {

     @Autowired
     private PlanRepository planRepository;

     @Autowired
     private ApiService apiService;

     @Autowired
     private AMQPCacheService amqpCacheService;

     @Transactional(readOnly = true)
     public Plan find(String id) {
          
          Plan plan = planRepository.findOne(id);
          HeimdallException.checkThrow(plan == null, GLOBAL_RESOURCE_NOT_FOUND);
                              
          return plan;
     }
     
     /**
      * Generates a paged list of {@link Plan} from a request.
      * 
      * @param  pageable					The {@link Pageable}
      * @return								The paged {@link Plan} list as a {@link PlanPage} object
      */
     @Transactional(readOnly = true)
     public Page<Plan> list(Pageable pageable) {

          return planRepository.findAll(pageable);
     }

     /**
      * Generates a list of {@link Plan} from a request.
      * 
      * @return								The List of {@link Plan}
      */
     @Transactional(readOnly = true)
     public List<Plan> list() {

          return planRepository.findAll();
     }
     
     /**
      * Saves a {@link Plan} to the repository.
      * 
      * @param  plan						The {@link Plan}
      * @return								The saved {@link Plan}
      */
     public Plan save(Plan plan) {

          if (plan.isDefaultPlan()) {
               Set<Plan> plans = apiService.plansByApi(plan.getApi().getId());
               HeimdallException.checkThrow(plans.stream().anyMatch(Plan::isDefaultPlan), DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API);
          }

          plan.setStatus(plan.getStatus() == null ? Status.ACTIVE : plan.getStatus());
          plan.setCreationDate(LocalDateTime.now());

          plan = planRepository.save(plan);

          Api api = apiService.find(plan.getApi().getId());

          api.addPlan(plan.getId());

          apiService.update(api);

          amqpCacheService.dispatchClean();

          return plan;
     }

     /**
      * Updates a {@link Plan} by its Id.
      * 
      * @param 	id							The {@link Plan} Id
      * @param 	planPersist					The {@link Plan}
      * @return								The updated {@link Plan}
      */
     public Plan update(final String id, final Plan planPersist) {

          Plan plan = this.find(id);

          if (planPersist.isDefaultPlan()) {
               Set<Plan> plans = apiService.plansByApi(plan.getApi().getId());
               HeimdallException.checkThrow(plans.stream().anyMatch(p -> !id.equals(p.getId()) && p.isDefaultPlan()), DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API);
          }

          plan = GenericConverter.mapper(planPersist, plan);

          plan = planRepository.save(plan);

          amqpCacheService.dispatchClean();

          return plan;
     }
     
     /**
      * Deletes a {@link Plan} by its Id.
      * 
      * @param 	id						The {@link Plan} Id
      */
     public void delete(String id) {

          Plan plan = this.find(id);

//          Integer totalAppsAttached = planRepository.findAppsWithPlan(id);
//          HeimdallException.checkThrow(totalAppsAttached > 0, PLAN_ATTACHED_TO_APPS);

          apiService.removePlan(plan);

          planRepository.delete(plan);

          amqpCacheService.dispatchClean();
     }

}
