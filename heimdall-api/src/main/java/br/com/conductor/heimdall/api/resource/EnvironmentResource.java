
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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_ENVIRONMENTS;

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
import br.com.conductor.heimdall.core.dto.EnvironmentDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.EnvironmentPage;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.service.EnvironmentService;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link EnvironmentService} to provide methods to create, read, update and delete a {@link Environment}.
 *
 * @author Filipe Germano
 *
 */
@io.swagger.annotations.Api(value = PATH_ENVIRONMENTS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_ENVIRONMENTS })
@RestController
@RequestMapping(value = PATH_ENVIRONMENTS)
public class EnvironmentResource {

     @Autowired
     private AMQPCacheService amqpCacheService;
	
     @Autowired
     private EnvironmentService environmentService;

     /**
      * Finds a {@link Environment} by its Id.
      * 
      * @param id					The Environment Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find Environment by id", response = Environment.class)
     @GetMapping(value = "/{environmentId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ENVIRONMENT)
     public ResponseEntity<?> findById(@PathVariable("environmentId") Long id) {

          Environment environment = environmentService.find(id);

          return ResponseEntity.ok(environment);
     }

     /**
      * Finds all {@link Environment} from a request.
      * 
      * @param environmentDTO		{@link EnvironmentDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Environments", responseContainer = "List", response = Environment.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ENVIRONMENT)
     public ResponseEntity<?> findAll(@ModelAttribute EnvironmentDTO environmentDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (Objeto.notBlank(pageableDTO)) {
               
               EnvironmentPage environmentPage = environmentService.list(environmentDTO, pageableDTO);      
               return ResponseEntity.ok(environmentPage);
          } else {
               
               List<Environment> environments = environmentService.list(environmentDTO);      
               return ResponseEntity.ok(environments);
          }
     }

     /**
      * Saves a {@link Environment}.
      * 
      * @param environmentDTO		{@link EnvironmentDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new Environment")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_ENVIRONMENT)
     public ResponseEntity<?> save(@RequestBody @Valid EnvironmentDTO environmentDTO) {

          Environment environment = environmentService.save(environmentDTO);
          
          return ResponseEntity.created(URI.create(String.format("/%s/%s", "environments", environment.getId().toString()))).build();
     }

     /**
      * Updates a {@link Environment}.
      * 
      * @param id					The Environment Id
      * @param environmentDTO		{@link EnvironmentDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update Environment")
     @PutMapping(value = "/{environmentId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_ENVIRONMENT)
     public ResponseEntity<?> update(@PathVariable("environmentId") Long id, @RequestBody EnvironmentDTO environmentDTO) {

    	  System.out.println("######");
    	  System.out.println(environmentDTO);
          Environment environment = environmentService.update(id, environmentDTO);
          amqpCacheService.dispatchClean();
          
          return ResponseEntity.ok(environment);
     }

     /**
      * Deletes a {@link Environment}.
      * 
      * @param id					The Environment Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete Environment")
     @DeleteMapping(value = "/{environmentId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_ENVIRONMENT)
     public ResponseEntity<?> delete(@PathVariable("environmentId") Long id) {

          environmentService.delete(id);
          amqpCacheService.dispatchClean();
          
          return ResponseEntity.noContent().build();
     }

}
