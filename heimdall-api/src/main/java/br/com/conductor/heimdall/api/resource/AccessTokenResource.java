
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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_ACCESS_TOKENS;

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
import br.com.conductor.heimdall.core.dto.page.AccessTokenPage;
import br.com.conductor.heimdall.core.dto.persist.AccessTokenPersist;
import br.com.conductor.heimdall.core.dto.request.AccessTokenRequest;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.service.AccessTokenService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link AccessTokenService} to provide methods to create, read, update and delete a {@link AccessToken}.
 *
 * @author Filipe Germano
 *
 */
@io.swagger.annotations.Api(value = PATH_ACCESS_TOKENS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_ACCESS_TOKENS })
@RestController
@RequestMapping(value = PATH_ACCESS_TOKENS)
public class AccessTokenResource {

     @Autowired
     private AccessTokenService accessTokenService;

     /**
      * Finds a {@link AccessToken} by its Id.
      * 
      * @param id					The AccessToken Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find AccessToken by id", response = AccessToken.class)
     @GetMapping(value = "/{accessTokenId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ACCESSTOKEN)
     public ResponseEntity<?> findById(@PathVariable("accessTokenId") Long id) {

          AccessToken accessToken = accessTokenService.find(id);

          return ResponseEntity.ok(accessToken);
     }

     /**
      * Finds all {@link AccessToken}
      * 
      * @param accessTokenRequest	{@link AccessTokenRequest}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all AccessTokens", responseContainer = "List", response = AccessToken.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ACCESSTOKEN)
     public ResponseEntity<?> findAll(@ModelAttribute AccessTokenRequest accessTokenRequest, @ModelAttribute PageableDTO pageableDTO) {
          
          if (Objeto.notBlank(pageableDTO)) {
               
               AccessTokenPage accessTokenPage = accessTokenService.list(accessTokenRequest, pageableDTO);      
               return ResponseEntity.ok(accessTokenPage);
          } else {
               
               List<AccessToken> accessTokens = accessTokenService.list(accessTokenRequest);      
               return ResponseEntity.ok(accessTokens);
          }
     }

     /**
      * Saves a {@link AccessToken}.
      * 
      * @param accessTokenPersist	{@link AccessTokenPersist}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new AccessToken")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_ACCESSTOKEN)
     public ResponseEntity<?> save(@RequestBody @Valid AccessTokenPersist accessTokenPersist) {

          AccessToken accessToken = accessTokenService.save(accessTokenPersist);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "access-tokens", accessToken.getId().toString()))).build();
     }

     /**
      * Updates a {@link AccessToken}.
      * 
      * @param id					The AccessToken Id
      * @param accessTokenPersist	{@link AccessTokenPersist}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update AccessToken")
     @PutMapping(value = "/{accessTokenId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_ACCESSTOKEN)
     public ResponseEntity<?> update(@PathVariable("accessTokenId") Long id, @RequestBody AccessTokenPersist accessTokenPersist) {

          AccessToken accessToken = accessTokenService.update(id, accessTokenPersist);
          
          return ResponseEntity.ok(accessToken);
     }

     /**
      * Deletes a {@link AccessToken}.
      * 
      * @param id					The AccessToken Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete AccessToken")
     @DeleteMapping(value = "/{accessTokenId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_ACCESSTOKEN)
     public ResponseEntity<?> delete(@PathVariable("accessTokenId") Long id) {

          accessTokenService.delete(id);
          
          return ResponseEntity.noContent().build();
     }

}
