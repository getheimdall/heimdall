/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ResourceDTO;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.service.ResourceService;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.conductor.heimdall.core.util.Pageable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_RESOURCES;

/**
 * Uses a {@link ResourceService} to provide methods to create, read, update and delete a {@link Resource}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 */
@io.swagger.annotations.Api(
        value = PATH_RESOURCES,
        produces = MediaType.APPLICATION_JSON_VALUE,
        tags = {ConstantsTag.TAG_RESOURCES})
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
    public ResponseEntity<?> findById(@PathVariable("apiId") String apiId,
                                      @PathVariable("resourceId") String resourceId) {

        Resource resource = resourceService.find(apiId, resourceId);

        return ResponseEntity.ok(resource);
    }

    /**
     * Finds all {@link Resource} from a request.
     *
     * @param apiId       The Api Id
     * @param pageableDTO {@link PageableDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Resources", responseContainer = "List", response = Resource.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_RESOURCE)
    public ResponseEntity<?> findAll(@PathVariable("apiId") String apiId,
                                     @ModelAttribute PageableDTO pageableDTO) {

        if (!pageableDTO.isEmpty()) {
            Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
            Page<Resource> resourcePage = resourceService.list(apiId, pageable);

            return ResponseEntity.ok(resourcePage);
        } else {
            List<Resource> resources = resourceService.list(apiId);

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
    public ResponseEntity<?> save(@PathVariable("apiId") String apiId,
                                  @RequestBody @Valid ResourceDTO resourceDTO) {

        Resource resource = GenericConverter.mapper(resourceDTO, Resource.class);
        resource = resourceService.save(apiId, resource);

        return ResponseEntity.created(URI.create(String.format("/%s/%s", "resources", resource.getId()))).build();
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
    public ResponseEntity<?> update(@PathVariable("apiId") String apiId,
                                    @PathVariable("resourceId") String resourceId,
                                    @RequestBody ResourceDTO resourceDTO) {

        Resource resource = GenericConverter.mapper(resourceDTO, Resource.class);
        resource = resourceService.update(apiId, resourceId, resource);

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
    public ResponseEntity<?> delete(@PathVariable("apiId") String apiId,
                                    @PathVariable("resourceId") String resourceId) {

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
