
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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;

import java.util.List;

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
     public Plan find(Long id) {
          
          Plan plan = planRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(plan), GLOBAL_RESOURCE_NOT_FOUND);
                              
          return plan;
     }
     
     /**
      * Generates a paged list of {@link Plan} from a request.
      * 
      * @param  planDTO						The {@link PlanDTO}
      * @param  pageableDTO					The {@link PageableDTO}
      * @return								The paged {@link Plan} list as a {@link PlanPage} object
      */
     @Transactional(readOnly = true)
     public PlanPage list(PlanDTO planDTO, PageableDTO pageableDTO) {

          Plan plan = GenericConverter.mapper(planDTO, Plan.class);
          
          Example<Plan> example = Example.of(plan, ExampleMatcher.matching().withIgnorePaths("defaultPlan").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<Plan> page = planRepository.findAll(example, pageable);
          
          PlanPage planPage = new PlanPage(PageDTO.build(page));
          
          return planPage;
     }

     /**
      * Generates a list of {@link Plan} from a request.
      * 
      * @param  planDTO						The {@link PlanDTO}
      * @return								The List of {@link Plan}
      */
     @Transactional(readOnly = true)
     public List<Plan> list(PlanDTO planDTO) {
          
          Plan plan = GenericConverter.mapper(planDTO, Plan.class);
          
          Example<Plan> example = Example.of(plan, ExampleMatcher.matching().withIgnorePaths("defaultPlan").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          List<Plan> plans = planRepository.findAll(example);
          
          return plans;
     }
     
     /**
      * Saves a {@link Plan} to the repository.
      * 
      * @param  planDTO						The {@link PlanDTO}
      * @return								The saved {@link Plan}
      */
     public Plan save(PlanDTO planDTO) {

          Plan plan = GenericConverter.mapper(planDTO, Plan.class);

          if (plan.isDefaultPlan()) {
               List<Plan> plans = apiService.plansByApi(plan.getApi().getId());
               HeimdallException.checkThrow(plans.stream().anyMatch(Plan::isDefaultPlan), DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API);
          }

          plan = planRepository.save(plan);

          amqpCacheService.dispatchClean();

          return plan;
     }

     /**
      * Updates a {@link Plan} by its Id.
      * 
      * @param 	id							The {@link Plan} Id
      * @param 	planDTO						The {@link PlanDTO}
      * @return								The updated {@link Plan}
      */
     public Plan update(Long id, PlanDTO planDTO) {

          Plan plan = planRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(plan), GLOBAL_RESOURCE_NOT_FOUND);
          
          plan = GenericConverter.mapper(planDTO, plan);

          final Long planId = plan.getId();

          if (plan.isDefaultPlan()) {
               List<Plan> plans = apiService.plansByApi(plan.getApi().getId());
               HeimdallException.checkThrow(plans.stream().anyMatch(p -> !p.getId().equals(planId) && p.isDefaultPlan()), DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API);
          }

          plan = planRepository.save(plan);

          amqpCacheService.dispatchClean();

          return plan;
     }
     
     /**
      * Deletes a {@link Plan} by its Id.
      * 
      * @param 	id						The {@link Plan} Id
      */
     public void delete(Long id) {

          Plan plan = planRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(plan), GLOBAL_RESOURCE_NOT_FOUND);
          
          planRepository.delete(plan);

          amqpCacheService.dispatchClean();
     }

}
