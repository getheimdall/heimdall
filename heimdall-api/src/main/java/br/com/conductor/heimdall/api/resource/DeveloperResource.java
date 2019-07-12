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
import br.com.conductor.heimdall.core.dto.DeveloperDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.request.DeveloperLogin;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.service.DeveloperService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Objects;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_DEVELOPERS;

/**
 * Uses a {@link DeveloperService} to provide methods to create, read, update and delete a {@link Developer}.
 *
 * @author Filipe Germano
 */
@io.swagger.annotations.Api(value = PATH_DEVELOPERS, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_DEVELOPERS})
@RestController
@RequestMapping(value = PATH_DEVELOPERS)
public class DeveloperResource {

    @Autowired
    private DeveloperService developerService;

    @ResponseBody
    @ApiOperation(value = "Find Developer by its email and password", response = Developer.class)
    @PostMapping("/login")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_DEVELOPER)
    public ResponseEntity login(@RequestBody DeveloperLogin developerLogin) {
        Developer developer = developerService.login(developerLogin);

        if (Objects.nonNull(developer)) {
            return ResponseEntity.ok(developer);
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Finds a {@link Developer} by its Id.
     *
     * @param id The Developer Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find Developer by id", response = Developer.class)
    @GetMapping(value = "/{developerId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_DEVELOPER)
    public ResponseEntity<?> findById(@PathVariable("developerId") String id) {

        Developer developer = developerService.find(id);

        return ResponseEntity.ok(developer);
    }

    /**
     * Finds all {@link Developer} from a request.
     *
     * @param pageableDTO {@link PageableDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Developers", responseContainer = "List", response = Developer.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_DEVELOPER)
    public ResponseEntity<?> findAll(@ModelAttribute PageableDTO pageableDTO) {

        if (!pageableDTO.isEmpty()) {

            final Pageable pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getLimit());
            Page<Developer> developerPage = developerService.list(pageable);

            return ResponseEntity.ok(developerPage);
        } else {

            List<Developer> developers = developerService.list();

            return ResponseEntity.ok(developers);
        }
    }

    /**
     * Saves a {@link Developer}.
     *
     * @param developerDTO {@link DeveloperDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Save a new Developer")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_DEVELOPER)
    public ResponseEntity<?> save(@RequestBody @Valid DeveloperDTO developerDTO) {

        Developer developer = GenericConverter.mapper(developerDTO, Developer.class);
        developer = developerService.save(developer);

        return ResponseEntity.created(URI.create(String.format("/%s/%s", "developers", developer.getId()))).build();
    }

    /**
     * Updates a {@link Developer}.
     *
     * @param id           The Developer Id
     * @param developerDTO {@link DeveloperDTO}
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Update Developer")
    @PutMapping(value = "/{developerId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_DEVELOPER)
    public ResponseEntity<?> update(@PathVariable("developerId") String id, @RequestBody DeveloperDTO developerDTO) {

        Developer developer = GenericConverter.mapper(developerDTO, Developer.class);
        developer = developerService.update(id, developer);

        return ResponseEntity.ok(developer);
    }

    /**
     * Deletes a {@link Developer}.
     *
     * @param id The Developer Id
     * @return                        {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Developer")
    @DeleteMapping(value = "/{developerId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_DEVELOPER)
    public ResponseEntity<?> delete(@PathVariable("developerId") String id) {

        developerService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @ApiOperation(value = "Find All Apps of a Developer")
    @GetMapping(value = "/{developerId}/apps")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_APP)
    public ResponseEntity<?> findByDeveloper(@PathVariable("developerId") String developerId) {

        final List<App> apps = developerService.list(developerId);

        return ResponseEntity.ok(apps);
    }
}
