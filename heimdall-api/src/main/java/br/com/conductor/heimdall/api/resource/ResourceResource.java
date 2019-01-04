
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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_RESOURCES;

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
import br.com.conductor.heimdall.core.dto.ResourceDTO;
import br.com.conductor.heimdall.core.dto.page.ResourcePage;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.service.ResourceService;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link ResourceService} to provide methods to create, read, update and delete a {@link Resource}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 */
@io.swagger.annotations.Api(value = PATH_RESOURCES, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_RESOURCES})
@RestController
@RequestMapping(value = PATH_RESOURCES)
public class ResourceResource {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private AMQPRouteService aMQPRouteService;

    /**
     * Finds a {@link Resource} by its Id.
     *
     * @param apiId      The Api Id
     * @param resourceId The Resource Id
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find Resource by id", response = Resource.class)
    @GetMapping(value = "/{resourceId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_RESOURCE)
    public ResponseEntity<?> findById(@PathVariable("apiId") Long apiId, @PathVariable("resourceId") Long resourceId) {

        Resource resource = resourceService.find(apiId, resourceId);

        return ResponseEntity.ok(resource);
    }

    /**
     * Finds all {@link Resource} from a request.
     *
     * @param apiId       The Api Id
     * @param resourceDTO {@link ResourceDTO}
     * @param pageableDTO {@link PageableDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Resources", responseContainer = "List", response = Resource.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_RESOURCE)
    public ResponseEntity<?> findAll(@PathVariable("apiId") Long apiId, @ModelAttribute ResourceDTO resourceDTO, @ModelAttribute PageableDTO pageableDTO) {

        if (Objeto.notBlank(pageableDTO)) {

            ResourcePage resourcePage = resourceService.list(apiId, resourceDTO, pageableDTO);
            return ResponseEntity.ok(resourcePage);
        } else {

            List<Resource> resources = resourceService.list(apiId, resourceDTO);
            return ResponseEntity.ok(resources);
        }
    }

    /**
     * Saves a {@link Resource}.
     *
     * @param apiId       The Api Id
     * @param resourceDTO {@link ResourceDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Save a new Resource")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_RESOURCE)
    public ResponseEntity<?> save(@PathVariable("apiId") Long apiId, @RequestBody @Valid ResourceDTO resourceDTO) {

        Resource resource = resourceService.save(apiId, resourceDTO);

        return ResponseEntity.created(URI.create(String.format("/%s/%s", "resources", resource.getId().toString()))).build();
    }

    /**
     * Updates a {@link Resource}.
     *
     * @param apiId       The Api Id
     * @param resourceId  The Resource Id
     * @param resourceDTO {@link ResourceDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Update Resource")
    @PutMapping(value = "/{resourceId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_RESOURCE)
    public ResponseEntity<?> update(@PathVariable("apiId") Long apiId, @PathVariable("resourceId") Long resourceId, @RequestBody ResourceDTO resourceDTO) {

        Resource resource = resourceService.update(apiId, resourceId, resourceDTO);

        return ResponseEntity.ok(resource);
    }

    /**
     * Deletes a {@link Resource}.
     *
     * @param apiId      The Api Id
     * @param resourceId The Resource Id
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Resource")
    @DeleteMapping(value = "/{resourceId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_RESOURCE)
    public ResponseEntity<?> delete(@PathVariable("apiId") Long apiId, @PathVariable("resourceId") Long resourceId) {

        resourceService.delete(apiId, resourceId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Refreshes all {@link Resource}.
     *
     * @param apiId The Api Id
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Refresh all Resources")
    @PostMapping(value = "/refresh")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_REFRESH_RESOURCE)
    public ResponseEntity<?> refresh(@PathVariable("apiId") Long apiId) {

        aMQPRouteService.dispatchRoutes();

        return ResponseEntity.noContent().build();
    }

}
