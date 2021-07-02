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
import br.com.heimdall.core.dto.InterceptorDTO;
import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.page.InterceptorPage;
import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.enums.TypeInterceptor;
import br.com.heimdall.core.service.InterceptorService;
import br.com.heimdall.core.service.amqp.AMQPInterceptorService;
import br.com.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static br.com.heimdall.core.util.ConstantsPath.PATH_INTERCEPTORS;

/**
 * Uses a {@link InterceptorService} to provide methods to create, read, update and delete a {@link Interceptor}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@io.swagger.annotations.Api(value = PATH_INTERCEPTORS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_INTERCEPTORS })
@RestController
@RequestMapping(value = PATH_INTERCEPTORS)
public class InterceptorResource {

     @Autowired
     private InterceptorService interceptorService;
     
     @Autowired
     private AMQPInterceptorService amqpInterceptorService;

     /**
      * Finds a {@link Interceptor} by its Id.
      * 
      * @param id					The Interceptor Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find API by id", response = Interceptor.class)
     @GetMapping(value = "/{interceptorId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_INTERCEPTOR)
     public ResponseEntity<Interceptor> findById(@PathVariable("interceptorId") Long id) {

          Interceptor interceptor = interceptorService.find(id);

          return ResponseEntity.ok(interceptor);
     }

     /**
      * Finds all {@link Interceptor} from a request.
      * 
      * @param interceptorDTO		{@link InterceptorDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Interceptors", responseContainer = "List", response = Interceptor.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_INTERCEPTOR)
     public ResponseEntity<?> findAll(@ModelAttribute InterceptorDTO interceptorDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (pageableDTO != null && !pageableDTO.isEmpty()) {
               
               InterceptorPage interceptorPage = interceptorService.list(interceptorDTO, pageableDTO);      
               return ResponseEntity.ok(interceptorPage);
          } else {
               
               List<Interceptor> interceptors = interceptorService.list(interceptorDTO);      
               return ResponseEntity.ok(interceptors);
          }
     }
     
     /**
      * Lists all types of {@link Interceptor}.
      * 
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "List all types of Interceptors", responseContainer = "List", response = Option.class)
     @GetMapping(value = "/types")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_INTERCEPTOR)
     public ResponseEntity<List<Option>> types() {

          List<Option> types = new ArrayList<>();

          for (TypeInterceptor type : TypeInterceptor.values()) {
               types.add(new Option(type.name()));
          }

          return ResponseEntity.ok(types);
     }

     /**
      * Saves a {@link Interceptor}.
      * 
      * @param interceptorDTO		{@link InterceptorDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new Interceptor")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_INTERCEPTOR)
     public ResponseEntity<String> save(@RequestBody @Valid InterceptorDTO interceptorDTO) {

          Interceptor interceptor = interceptorService.save(interceptorDTO); 

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "interceptors", interceptor.getId().toString()))).build();
     }

     /**
      * Updates a {@link Interceptor}.
      * 
      * @param id					The Interceptor Id
      * @param interceptorDTO		{@link InterceptorDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update Interceptor")
     @PutMapping(value = "/{interceptorId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_INTERCEPTOR)
     public ResponseEntity<Interceptor> update(@PathVariable("interceptorId") Long id, @RequestBody InterceptorDTO interceptorDTO) {

          Interceptor interceptor = interceptorService.update(id, interceptorDTO);
          
          return ResponseEntity.ok(interceptor);
     }

     /**
      * Delets a {@link Interceptor}
      * 
      * @param id					The Interceptor Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete Interceptor")
     @DeleteMapping(value = "/{interceptorId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_INTERCEPTOR)
     public ResponseEntity<Void> delete(@PathVariable("interceptorId") Long id) {

          interceptorService.delete(id);
          
          return ResponseEntity.noContent().build();
     }

     /**
      * Refreshes all {@link Interceptor}.
      * 
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Refresh Interceptor")
     @PostMapping(value = "/refresh")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_REFRESH_INTERCEPTOR)
     public ResponseEntity<Void> refresh() {
          
          amqpInterceptorService.dispatchRefreshAllInterceptors();
          
          return ResponseEntity.noContent().build();
     }
     
     /*
      * Data class that holds the Option type.
      */
     @Data
     @AllArgsConstructor
     private class Option {
          private String type;
     }

}
