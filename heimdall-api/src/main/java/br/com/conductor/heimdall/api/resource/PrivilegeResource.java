
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

import br.com.conductor.heimdall.api.dto.PrivilegeDTO;
import br.com.conductor.heimdall.api.dto.page.PrivilegePage;
import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.service.PrivilegeService;
import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_PRIVILEGES;

/**
 * Uses a {@link PrivilegeService} to provide methods to find one or more {@link Privilege}.
 *
 * @author Marcos Filho
 *
 */
@io.swagger.annotations.Api(value = PATH_PRIVILEGES, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_PRIVILEGES })
@RestController
@RequestMapping(value = PATH_PRIVILEGES)
public class PrivilegeResource {

     @Autowired
     private PrivilegeService privilegeService;

     /**
      * Finds a {@link Privilege} by its Id.
      * 
      * @param id					The Privilege Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find Privileges by id", response = Privilege.class)
     @GetMapping(value = "/{apiId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PRIVILEGE)
     public ResponseEntity<?> findById(@PathVariable("apiId") Long id) {

          Privilege privilege = privilegeService.find(id);

          return ResponseEntity.ok(privilege);
     }

     /**
      * Finds all {@link Privilege} from a request.
      * 
      * @param privilegeDTO			{@link PrivilegeDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Privileges", responseContainer = "List", response = Privilege.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PRIVILEGE)
     public ResponseEntity<?> findAll(@ModelAttribute PrivilegeDTO privilegeDTO, @ModelAttribute PageableDTO pageableDTO) {

          if (Objeto.notBlank(pageableDTO)) {

               PrivilegePage apiPage = privilegeService.list(privilegeDTO, pageableDTO);
               return ResponseEntity.ok(apiPage);
          } else {

               List<Privilege> privileges = privilegeService.list(privilegeDTO);
               return ResponseEntity.ok(privileges);
          }

     }

     @ResponseBody
     @ApiOperation(value = "Find all Privileges by username", responseContainer = "List", response = Privilege.class)
     @PostMapping("/username")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PRIVILEGE)
     public ResponseEntity findPrivilegesByUsername(@RequestBody String username) {

          Set<Privilege> list = privilegeService.list(username);

          if (list.isEmpty()){
               return ResponseEntity.notFound().build();
          }

          return ResponseEntity.ok(list);
     }
}
