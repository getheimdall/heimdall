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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_APPS;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import br.com.conductor.heimdall.core.dto.persist.AppPersist;
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
import br.com.conductor.heimdall.core.dto.AppDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.AppPage;
import br.com.conductor.heimdall.core.dto.request.AppRequestDTO;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.service.AppService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses the {@link AppService} to provide methods to create, read, update and delete a {@link App}.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@io.swagger.annotations.Api(value = PATH_APPS, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_APPS })
@RestController
@RequestMapping(value = PATH_APPS)
public class AppResource {

     @Autowired
     private AppService appService;

     /**
      * Finds a {@link App} by its Id.
      * 
      * @param id					The App Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find App by id", response = App.class)
     @GetMapping(value = "/{appId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_APP)
     public ResponseEntity<?> findById(@PathVariable("appId") Long id) {

          App app = appService.find(id);

          return ResponseEntity.ok(app);
     }

     /**
      * Finds all {@link App} from a request.
      * 
      * @param appDTO				{@link AppDTO}
      * @param pageableDTO			{@link PageableDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Find all Apps", responseContainer = "List", response = App.class)
     @GetMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_APP)
     public ResponseEntity<?> findAll(@ModelAttribute AppRequestDTO appDTO, @ModelAttribute PageableDTO pageableDTO) {
          
          if (Objeto.notBlank(pageableDTO)) {
               
               AppPage appPage = appService.list(appDTO, pageableDTO);
               
               if (!appPage.getContent().isEmpty()) {
                    List<App> apps = appPage.getContent();
                    apps = apps.stream().map(app -> new App(app.getId(), app.getClientId(), app.getName(), app.getDescription(), app.getDeveloper(), app.getCreationDate(), app.getStatus(), null, app.getPlans(), null, app.getTags())).collect(Collectors.toList());
                    appPage.setContent(apps);
               }
               return ResponseEntity.ok(appPage);
          } else {
               
               List<App> apps = appService.list(appDTO);
               
               if (!apps.isEmpty()) {
                    apps = apps.stream().map(app -> new App(app.getId(), app.getClientId(), app.getName(), app.getDescription(), app.getDeveloper(), app.getCreationDate(), app.getStatus(), null, app.getPlans(), null, app.getTags())).collect(Collectors.toList());
               }
               return ResponseEntity.ok(apps);
          }
     }

     /**
      * Saves a {@link App}.
      * 
      * @param appDTO				{@link AppDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Save a new App")
     @PostMapping
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_APP)
     public ResponseEntity<?> save(@RequestBody @Valid AppPersist appDTO) {

          App app = appService.save(appDTO);

          return ResponseEntity.created(URI.create(String.format("/%s/%s", "apps", app.getId().toString()))).build();
     }

     /**
      * Updates a {@link App}.
      * 
      * @param id					The App Id
      * @param appDTO				{@link AppDTO}
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Update App")
     @PutMapping(value = "/{appId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_APP)
     public ResponseEntity<?> update(@PathVariable("appId") Long id, @RequestBody AppDTO appDTO) {

          App app = appService.update(id, appDTO);
          
          return ResponseEntity.ok(app);
     }

     /**
      * Deletes a {@link App}.
      * 
      * @param id					The App Id
      * @return						{@link ResponseEntity}
      */
     @ResponseBody
     @ApiOperation(value = "Delete App")
     @DeleteMapping(value = "/{appId}")
     @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_APP)
     public ResponseEntity<?> delete(@PathVariable("appId") Long id) {

          appService.delete(id);
          
          return ResponseEntity.noContent().build();
     }

}
