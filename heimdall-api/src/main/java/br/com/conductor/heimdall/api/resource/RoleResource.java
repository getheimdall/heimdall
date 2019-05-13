/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.dto.RoleDTO;
import br.com.conductor.heimdall.api.dto.page.RolePage;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.service.RoleService;
import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_ROLES;

/**
 * Uses a {@link RoleService} to provide methods to create, read, update and delete a {@link Role}.
 *
 * @author Marcos Filho
 *
 */
@io.swagger.annotations.Api(value = PATH_ROLES, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_ROLES })
@RestController
@RequestMapping(value = PATH_ROLES)
public class RoleResource {

     @Autowired
     private RoleService roleService;    

     /**
      * Saves a {@link Role}.
      * 
      * @param roleDTO				{@link RoleDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new Role")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_ROLE)
     public ResponseEntity<?> save(@RequestBody @Valid RoleDTO roleDTO) {

          Role role = roleService.save(roleDTO);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "roles", role.getId().toString()))).build();
     }
     
     /**
      * Finds a {@link Role} by its Id.
      * 
      * @param roleId				The Role Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find Role by id", response = Role.class)
     @GetMapping(value = "/{roleId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ROLE)
     public ResponseEntity<?> findById(@PathVariable("roleId") Long roleId) {

          Role resource = roleService.find(roleId);

          return ResponseEntity.ok(resource);
     }

     /**
      * Finds all {@link Role} from a request.
      * 
      * @param roleDTO				{@link RoleDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Resources", responseContainer = "List", response = Role.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ROLE)
     public ResponseEntity<?> findAll(@ModelAttribute RoleDTO roleDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (!pageableDTO.isEmpty()) {
               
               RolePage resourcePage = roleService.list(roleDTO, pageableDTO);      
               return ResponseEntity.ok(resourcePage);
          } else {
               
               List<Role> resources = roleService.list(roleDTO);      
               return ResponseEntity.ok(resources);
          }
     }
     
     /**
      * Updates a {@link Role}.
      * 
      * @param roleId				The Role Id
      * @param roleDTO				{@link RoleDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update Role")
     @PutMapping(value = "/{roleId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_ROLE)
     public ResponseEntity<?> update(@PathVariable("roleId") Long roleId, @RequestBody RoleDTO roleDTO) {

          Role role = roleService.update(roleId, roleDTO);

          return ResponseEntity.ok(role);
     }
     
     /**
      * Deletes a {@link Role}.
      * 
      * @param roleId				The Role Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete Role")
     @DeleteMapping(value = "/{roleId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_ROLE)
     public ResponseEntity<?> delete( @PathVariable("roleId") Long roleId) {

          roleService.delete(roleId);

          return ResponseEntity.noContent().build();
     }
}
