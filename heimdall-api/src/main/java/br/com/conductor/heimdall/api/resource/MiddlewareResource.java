
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

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.MiddlewareDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.MiddlewarePage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.service.MiddlewareService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import com.google.common.net.HttpHeaders;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_MIDDLEWARES;

/**
 * Uses a {@link MiddlewareService} to provide methods to create, read, update and delete a {@link Middleware}.
 *
 * @author Filipe Germano
 *
 */
@io.swagger.annotations.Api(value = PATH_MIDDLEWARES, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_MIDDLEWARES })
@RestController
@RequestMapping(value = PATH_MIDDLEWARES)
public class MiddlewareResource {

     @Autowired
     private MiddlewareService middlewareService;

     /**
      * Finds a {@link Middleware} by its Id and {@link Api} Id.
      *
      * @param apiId				The Api Id
      * @param middlewareId			The Middleware Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find Middleware by id", response = Api.class)
     @GetMapping(value = "/{middlewareId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_MIDDLEWARE)
     public ResponseEntity<?> findById(@PathVariable("apiId") Long apiId, @PathVariable("middlewareId") Long middlewareId) {

          Middleware middleware = middlewareService.find(apiId, middlewareId);

          return ResponseEntity.ok(middleware);
     }

     /**
      * Finds all {@link Middleware} from a request.
      *
      * @param apiId				The Api Id
      * @param middlewareDTO		{@link MiddlewareDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Middlewares", responseContainer = "List", response = Middleware.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_MIDDLEWARE)
     public ResponseEntity<?> findAll(@PathVariable("apiId") Long apiId, @ModelAttribute MiddlewareDTO middlewareDTO, @ModelAttribute PageableDTO pageableDTO) {

          if (Objeto.notBlank(pageableDTO)) {

               MiddlewarePage middlewarePage = middlewareService.list(apiId, middlewareDTO, pageableDTO);
               return ResponseEntity.ok(middlewarePage);
          } else {

               List<Middleware> middlewares = middlewareService.list(apiId, middlewareDTO);
               return ResponseEntity.ok(middlewares);
          }

     }

     /**
      * Saves a {@link Middleware}.
      *
      * @param apiId				The Api Id
      * @param file					{@link MultipartFile} of the Api
      * @param middlewareDTO		{@link MiddlewareDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new Middleware")
     @PostMapping(consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE })
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_MIDDLEWARE)
     public ResponseEntity<?> save(@PathVariable("apiId") Long apiId,
               @RequestParam("file") MultipartFile file,
               @Valid
               MiddlewareDTO middlewareDTO) {

          Middleware middleware = middlewareService.save(apiId, middlewareDTO, file);

          return ResponseEntity.created(URI.create(String.format("/%s/%s/%s/%s", "api", apiId.toString(), "middlewares", middleware.getId().toString()))).build();
     }

     /**
      * Updates a {@link Middleware}.
      *
      * @param apiId				The Api Id
      * @param middlewareId			The Middleware Id
      * @param middlewareDTO		{@link MiddlewareDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update Middleware")
     @PutMapping(value = "/{middlewareId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_MIDDLEWARE)
     public ResponseEntity<?> update(@PathVariable("apiId") Long apiId, @PathVariable("middlewareId") Long middlewareId, @RequestBody MiddlewareDTO middlewareDTO) {

          Middleware middleware = middlewareService.update(apiId, middlewareId, middlewareDTO);

          return ResponseEntity.ok(middleware);
     }

     /**
      * Deletes a {@link Middleware}.
      *
      * @param apiId				The Api Id
      * @param middlewareId			The Middleware Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete Middleware")
     @DeleteMapping(value = "/{middlewareId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_MIDDLEWARE)
     public ResponseEntity<?> delete(@PathVariable("apiId") Long apiId, @PathVariable("middlewareId") Long middlewareId) {

          middlewareService.delete(apiId, middlewareId);

          return ResponseEntity.noContent().build();
     }

     /**
      * Downloads a {@link Middleware} file by its Id and {@link Api} Id.
      *
      * @param apiId				The Api Id
      * @param middlewareId			The Middleware Id
      * @return						{@link ResponseEntity}
      */
     @ApiOperation(value = "Download Middleware file by id", response = Api.class)
     @GetMapping(value = "/download/{middlewareId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_MIDDLEWARE)
     public ResponseEntity<?> downloadFileById(@PathVariable("apiId") Long apiId, @PathVariable("middlewareId") Long middlewareId){

          Middleware middleware = middlewareService.find(apiId, middlewareId);
          ByteArrayResource resource = new ByteArrayResource(middleware.getFile());

          return ResponseEntity.ok()
                  .contentType(MediaType.APPLICATION_OCTET_STREAM)
                  .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + middleware.getName() + "-" +  middleware.getVersion() + "." + middleware.getType())
                  .header("filename", middleware.getName() + "-" +  middleware.getVersion() + "." + middleware.getType())
                  .body(resource);
     }

}
