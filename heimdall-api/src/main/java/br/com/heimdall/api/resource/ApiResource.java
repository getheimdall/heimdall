/*-
 * =========================LICENSE_START==================================
 * heimdall-api
 * ========================================================================
 *
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
package br.com.heimdall.api.resource;

import br.com.heimdall.api.util.ConstantsPrivilege;
import br.com.heimdall.core.dto.ApiDTO;
import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.page.ApiPage;
import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.service.ApiService;
import br.com.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.Swagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.heimdall.core.util.ConstantsPath.PATH_APIS;

/**
 * Uses a {@link ApiService} to provide methods to create, read, update and delete a {@link Api}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@io.swagger.annotations.Api(value = PATH_APIS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_APIS })
@RestController
@RequestMapping(value = PATH_APIS)

public class ApiResource {

     @Autowired
     private ApiService apiService;
     
     /**
      * Finds a {@link Api} by its Id.
      * 
      * @param id					The Api Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find API by id", response = Api.class)
     @GetMapping(value = "/{apiId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_API)
     public ResponseEntity<Api> findById(@PathVariable("apiId") Long id) {

          Api api = apiService.find(id);
          return ResponseEntity.ok(api);
     }

     /**
      * Get {@link Swagger} by {@link Api} its Id.
      *
      * @param id					The Api Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Get SwaggerJson by Api Id", response = Api.class)
     @GetMapping(value = "/{apiId}/swagger")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_API)
     public ResponseEntity<Swagger> getSwaggerByApiId(@PathVariable("apiId") Long id) {

          Swagger swagger = apiService.findSwaggerByApi(id);

          return ResponseEntity.ok(swagger);
     }

     /**
      * Fids all {@link Api} from a request.
      * 
      * @param apiDTO				{@link ApiDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all APIs", responseContainer = "List", response = Api.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_API)
     public ResponseEntity<?> findAll(@ModelAttribute ApiDTO apiDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (!pageableDTO.isEmpty()) {
               
               ApiPage apiPage = apiService.list(apiDTO, pageableDTO);      
               return ResponseEntity.ok(apiPage);
          } else {
               
               List<Api> apis = apiService.list(apiDTO);      
               return ResponseEntity.ok(apis);
          }
          
     }

     /**
      * Saves a {@link Api}.
      * 
      * @param apiDTO				{@link ApiDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new API")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_API)
     public ResponseEntity<String> save(@RequestBody @Valid ApiDTO apiDTO) {

          Api api = apiService.save(apiDTO);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "apis", api.getId().toString()))).build();
     }

     /**
      * Updates a {@link Api}.
      * 
      * @param id					The Api Id
      * @param apiDTO				{@link ApiDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update API")
     @PutMapping(value = "/{apiId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_API)
     public ResponseEntity<Api> update(@PathVariable("apiId") Long id, @RequestBody ApiDTO apiDTO) {

          Api api = apiService.update(id, apiDTO);
          
          return ResponseEntity.ok(api);
     }

     /**
      * Deletes a {@link Api}.
      * 
      * @param id					The Api Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete API")
     @DeleteMapping(value = "/{apiId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_API)
     public ResponseEntity<Void> delete(@PathVariable("apiId") Long id) {

          apiService.delete(id);
          
          return ResponseEntity.noContent().build();
     }

     /**
      * Uploads the {@link Api} file.
      * 
      * @param id					The Api Id
      * @param file					{@link MultipartFile} of the Api
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete API")
     @PostMapping(value = "/{apiId}/file-middlewares")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_API)
     public ResponseEntity<Void> fileUpload(@PathVariable("apiId") Long id,
               @RequestParam("file") MultipartFile file) {
          
          return ResponseEntity.noContent().build();
     }

     @ApiOperation(value = "Update API by Swagger JSON")
     @PutMapping("/{apiId}/swagger")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_API)
     public ResponseEntity<Api> updateApiBySwaggerJSON(@PathVariable("apiId") Long id, @RequestBody String swagger, boolean override) {
          Api api = apiService.updateBySwagger(id, swagger, override);
          return ResponseEntity.ok(api);
     }
}
