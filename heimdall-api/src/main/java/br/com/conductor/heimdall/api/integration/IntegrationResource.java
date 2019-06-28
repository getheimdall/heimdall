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
package br.com.conductor.heimdall.api.integration;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conductor.heimdall.core.dto.DeveloperDTO;
import br.com.conductor.heimdall.core.dto.integration.AccessTokenDTO;
import br.com.conductor.heimdall.core.dto.integration.AppCallbackDTO;
import br.com.conductor.heimdall.core.service.AccessTokenService;
import br.com.conductor.heimdall.core.service.AppService;
import br.com.conductor.heimdall.core.service.DeveloperService;
import br.com.conductor.heimdall.core.util.ConstantsPath;

/**
 * Uses a {@link AccessTokenService}, {@link DeveloperService} and {@link AppService} to provide the callback requests
 *
 * @author Marcos Filho
 *
 */
@RestController
@RequestMapping(value = ConstantsPath.PATH_INTEGRATION_RESOURCES, produces = MediaType.APPLICATION_JSON_VALUE)
public class IntegrationResource {
     
     @Autowired
     private AccessTokenService tokenService;
     
     @Autowired
     private DeveloperService developerService;
     
     @Autowired
     private AppService appService;
     
     /**
      * AccessToken callback.
      * 
      * @param reqBody	{@link AccessTokenDTO}
      * @return			{@link ResponseEntity}
      */
     @PostMapping(value = "/access-token/callback", produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<?> accessTokenCallback(@RequestBody @Valid AccessTokenDTO reqBody) {
          
//          tokenService.save(reqBody);
          return ResponseEntity.ok().build();
     }
     
     /**
      * Developer callback.
      * 
      * @param reqBody {@link DeveloperDTO}
      * @return			{@link ResponseEntity}
      */
     @PostMapping(value = "/developer/callback", produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<?> developerCallback(@RequestBody @Valid DeveloperDTO reqBody) {
          
          developerService.save(reqBody);
          return ResponseEntity.ok().build();
     }
     
     /**
      * App callback. 
      *
      * @param reqBody	{@link AppCallbackDTO}
      * @return			{@link ResponseEntity}
      */
     @PostMapping(value = "/app/callback", produces = MediaType.APPLICATION_JSON_VALUE)
     public ResponseEntity<?> appCallback(@RequestBody @Valid AppCallbackDTO reqBody) {
          
          appService.save(reqBody);
          return ResponseEntity.ok().build();
     }
}
