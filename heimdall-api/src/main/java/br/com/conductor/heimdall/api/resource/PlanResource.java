
package br.com.conductor.heimdall.api.resource;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_PLANS;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.PlanDTO;
import br.com.conductor.heimdall.core.dto.page.PlanPage;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.service.PlanService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link PlanService} to provide methods to create, read, update and delete a {@link Plan}.
 *
 * @author Filipe Germano
 *
 */
@io.swagger.annotations.Api(value = PATH_PLANS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_PLANS })
@RestController
@RequestMapping(value = PATH_PLANS)
public class PlanResource {

     @Autowired
     private PlanService planService;

     /**
      * Finds a {@link Plan} by its Id.
      * 
      * @param id					The Plan Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find Plan by id", response = Plan.class)
     @GetMapping(value = "/{planId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PLAN)
     public ResponseEntity<?> findById(@PathVariable("planId") Long id) {

          Plan plan = planService.find(id);

          return ResponseEntity.ok(plan);
     }

     /**
      * Finds all {@link Plan} from a request.
      * 
      * @param planDTO				{@link PlanDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Plans", responseContainer = "List", response = Plan.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PLAN)
     public ResponseEntity<?> findAll(@ModelAttribute PlanDTO planDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (Objeto.notBlank(pageableDTO)) {
               
               PlanPage planPage = planService.list(planDTO, pageableDTO);      
               return ResponseEntity.ok(planPage);
          } else {
               
               List<Plan> plans = planService.list(planDTO);      
               return ResponseEntity.ok(plans);
          }
     }

     /**
      * Saves a {@link Plan}.
      * 
      * @param planDTO				{@link PlanDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new Plan")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_PLAN)
     public ResponseEntity<?> save(@RequestBody @Valid PlanDTO planDTO) {

          Plan plan = planService.save(planDTO);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "plans", plan.getId().toString()))).build();
     }

     /**
      * Updates a {@link Plan}.
      * 
      * @param id					The Plan Id
      * @param planDTO				{@link PlanDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update Plan")
     @PutMapping(value = "/{planId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_PLAN)
     public ResponseEntity<?> update(@PathVariable("planId") Long id, @RequestBody PlanDTO planDTO) {

          Plan plan = planService.update(id, planDTO);
          
          return ResponseEntity.ok(plan);
     }

     /**
      * Deletes a {@link Plan}.
      * 
      * @param id					The Plan Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete Plan")
     @DeleteMapping(value = "/{planId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_PLAN)
     public ResponseEntity<?> delete(@PathVariable("planId") Long id) {

          planService.delete(id);
          
          return ResponseEntity.noContent().build();
     }

}
