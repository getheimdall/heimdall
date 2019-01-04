
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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_USERS;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import br.com.conductor.heimdall.api.dto.UserEditDTO;
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

import br.com.conductor.heimdall.api.dto.UserDTO;
import br.com.conductor.heimdall.api.dto.page.UserPage;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.service.UserService;
import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link UserService} to provide methods to create, read, update and delete a {@link User}.
 *
 * @author Marcos Filho
 *
 */
@io.swagger.annotations.Api(value = PATH_USERS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_USERS })
@RestController
@RequestMapping(value = PATH_USERS)
public class UserResource {

     @Autowired
     private UserService userService;    

     /**
      * Saves a {@link User}.
      * 
      * @param userDTO				{@link UserDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new User")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_USER)
     public ResponseEntity<?> save(@RequestBody @Valid UserDTO userDTO) {

          User user = userService.save(userDTO);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "users", user.getId().toString()))).build();
     }
     
     /**
      * Finds a {@link User} by its Id.
      * 
      * @param userId				The User Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find User by id", response = User.class)
     @GetMapping(value = "/{userId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_USER)
     public ResponseEntity<?> findById(@PathVariable("userId") Long userId) {

          User user = userService.find(userId);

          return ResponseEntity.ok(user);
     }

     /**
      * Finds all {@link User} from a request.
      * 
      * @param userDTO				{@link UserDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Users", responseContainer = "List", response = User.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_USER)
     public ResponseEntity<?> findAll(@ModelAttribute UserDTO userDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (Objeto.notBlank(pageableDTO)) {
               
               UserPage userPage = userService.list(userDTO, pageableDTO);
               if (!userPage.getContent().isEmpty()) {
                    List<User> users = userPage.getContent();
                    users = users.stream().map(user -> new User(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail(), user.getPassword(), user.getStatus(), user.getCreationDate(), user.getType(), null)).collect(Collectors.toList());
                    userPage.setContent(users);
               }
               return ResponseEntity.ok(userPage);
          } else {
               
               List<User> users = userService.list(userDTO);
               if (!users.isEmpty()) {
                    users = users.stream().map(user -> new User(user.getId(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail(), user.getPassword(), user.getStatus(), user.getCreationDate(), user.getType(), null)).collect(Collectors.toList());
               }
               return ResponseEntity.ok(users);
          }
     }
     
     /**
      * Updates a {@link User}.
      * 
      * @param userId				The User Id
      * @param userDTO				{@link UserEditDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update User")
     @PutMapping(value = "/{userId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_USER)
     public ResponseEntity<?> update(@PathVariable("userId") Long userId, @RequestBody UserEditDTO userDTO) {

          User user = userService.update(userId, userDTO);

          return ResponseEntity.ok(user);
     }
     
     /**
      * Deletes a {@link User}.
      * 
      * @param userId				The User Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete User")
     @DeleteMapping(value = "/{userId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_USER)
     public ResponseEntity<?> delete( @PathVariable("userId") Long userId) {

          userService.delete(userId);

          return ResponseEntity.noContent().build();
     }
     
}
