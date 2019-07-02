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
import br.com.conductor.heimdall.core.dto.EnvironmentDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.EnvironmentPage;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.service.EnvironmentService;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_ENVIRONMENTS;

/**
 * Uses a {@link EnvironmentService} to provide methods to create, read, update and delete a {@link Environment}.
 *
 * @author Filipe Germano
 */
@io.swagger.annotations.Api(value = PATH_ENVIRONMENTS, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_ENVIRONMENTS})
@RestController
@RequestMapping(value = PATH_ENVIRONMENTS)
public class EnvironmentResource {

    @Autowired
    private AMQPCacheService amqpCacheService;

    @Autowired
    private EnvironmentService environmentService;

    /**
     * Finds a {@link Environment} by its Id.
     *
     * @param id The Environment Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find Environment by id", response = Environment.class)
    @GetMapping(value = "/{environmentId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ENVIRONMENT)
    public ResponseEntity<?> findById(@PathVariable("environmentId") String id) {

        Environment environment = environmentService.find(id);

        return ResponseEntity.ok(environment);
    }

    /**
     * Finds all {@link Environment} from a request.
     *
     * @param pageableDTO {@link PageableDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Environments", responseContainer = "List", response = Environment.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_ENVIRONMENT)
    public ResponseEntity<?> findAll(@ModelAttribute PageableDTO pageableDTO) {

        if (!pageableDTO.isEmpty()) {

            Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
            final Page<Environment> environments = environmentService.list(pageable);

            return ResponseEntity.ok(environments);
        } else {

            final List<Environment> environments = environmentService.list();
            return ResponseEntity.ok(environments);
        }
    }

    /**
     * Saves a {@link Environment}.
     *
     * @param environmentDTO {@link EnvironmentDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Save a new Environment")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_ENVIRONMENT)
    public ResponseEntity<?> save(@RequestBody @Valid EnvironmentDTO environmentDTO) {

        Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);

        environment = environmentService.save(environment);

        return ResponseEntity.created(URI.create(String.format("/%s/%s", "environments", environment.getId()))).build();
    }

    /**
     * Updates a {@link Environment}.
     *
     * @param id             The Environment Id
     * @param environmentDTO {@link EnvironmentDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Update Environment")
    @PutMapping(value = "/{environmentId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_ENVIRONMENT)
    public ResponseEntity<?> update(@PathVariable("environmentId") String id, @RequestBody EnvironmentDTO environmentDTO) {

        Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);

        environment = environmentService.update(id, environment);
        amqpCacheService.dispatchClean();

        return ResponseEntity.ok(environment);
    }

    /**
     * Deletes a {@link Environment}.
     *
     * @param id The Environment Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Environment")
    @DeleteMapping(value = "/{environmentId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_ENVIRONMENT)
    public ResponseEntity<?> delete(@PathVariable("environmentId") String id) {

        environmentService.delete(id);
        amqpCacheService.dispatchClean();

        return ResponseEntity.noContent().build();
    }

}
