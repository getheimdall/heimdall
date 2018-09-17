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
package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.DeveloperDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.DeveloperPage;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.service.DeveloperService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import com.github.thiagonego.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_DEVELOPERS;

/**
 * Uses a {@link DeveloperService} to provide methods to create, read, update and delete a {@link Developer}.
 *
 * @author Filipe Germano
 *
 */
@io.swagger.annotations.Api(value = PATH_DEVELOPERS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_DEVELOPERS })
@RestController
@RequestMapping(value = PATH_DEVELOPERS)
public class DeveloperResource {

     @Autowired
     private DeveloperService developerService;

     /**
      * Finds a {@link Developer} by its Id.
      * 
      * @param id					The Developer Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find Developer by id", response = Developer.class)
     @GetMapping(value = "/{developerId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_DEVELOPER)
     public ResponseEntity<?> findById(@PathVariable("developerId") Long id) {

          Developer developer = developerService.find(id);

          return ResponseEntity.ok(developer);
     }

     /**
      * Finds all {@link Developer} from a request.
      * 
      * @param developerDTO			{@link DeveloperDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Developers", responseContainer = "List", response = Developer.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_DEVELOPER)
     public ResponseEntity<?> findAll(@ModelAttribute DeveloperDTO developerDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (Objeto.notBlank(pageableDTO)) {
               
               DeveloperPage developerPage = developerService.list(developerDTO, pageableDTO);      
               return ResponseEntity.ok(developerPage);
          } else {
               
               List<Developer> developers = developerService.list(developerDTO);      
               return ResponseEntity.ok(developers);
          }
     }

     /**
      * Saves a {@link Developer}.
      * 
      * @param developerDTO			{@link DeveloperDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new Developer")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_DEVELOPER)
     public ResponseEntity<?> save(@RequestBody @Valid DeveloperDTO developerDTO) {

          Developer developer = developerService.save(developerDTO);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "developers", developer.getId().toString()))).build();
     }

     /**
      * Updates a {@link Developer}.
      * 
      * @param id					The Developer Id
      * @param developerDTO			{@link DeveloperDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update Developer")
     @PutMapping(value = "/{developerId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_DEVELOPER)
     public ResponseEntity<?> update(@PathVariable("developerId") Long id, @RequestBody DeveloperDTO developerDTO) {

          Developer developer = developerService.update(id, developerDTO);
          
          return ResponseEntity.ok(developer);
     }

     /**
      * Deletes a {@link Developer}.
      * 
      * @param id					The Developer Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete Developer")
     @DeleteMapping(value = "/{developerId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_DEVELOPER)
     public ResponseEntity<?> delete(@PathVariable("developerId") Long id) {

          developerService.delete(id);
          
          return ResponseEntity.noContent().build();
     }

}
