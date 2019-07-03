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
import br.com.conductor.heimdall.core.dto.ScopeDTO;
import br.com.conductor.heimdall.core.entity.Scope;
import br.com.conductor.heimdall.core.service.ScopeService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.conductor.heimdall.core.util.Pageable;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_SCOPES;

/**
 * Uses a {@link ScopeService} to provide methods to create, read, update and delete a {@link Scope}
 *
 * @author Marcelo Aguiar Rodrigues
 */
@io.swagger.annotations.Api(value = PATH_SCOPES, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_SCOPES})
@RestController
@RequestMapping(value = PATH_SCOPES)
public class ScopeResource {

    @Autowired
    private ScopeService scopeService;

    /**
     * Finds a {@link Scope} by its Id.
     *
     * @param apiId   The Api Id
     * @param scopeId The scope Id
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find Scope by id", response = Scope.class)
    @GetMapping(value = "/{scopeId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_SCOPE)
    public ResponseEntity<?> findById(@PathVariable("apiId") String apiId,
                                      @PathVariable("scopeId") String scopeId) {

        Scope scope = scopeService.find(apiId, scopeId);

        return ResponseEntity.ok(scope);
    }

    /**
     * Saves a {@link Scope}.
     *
     * @param apiId    The Api Id
     * @param scopeDTO {@link ScopeDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Save a new Scope")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_SCOPE)
    public ResponseEntity<?> save(@PathVariable("apiId") String apiId,
                                  @RequestBody @Valid ScopeDTO scopeDTO) {

        Scope scope = GenericConverter.mapper(scopeDTO, Scope.class);

        scope = scopeService.save(apiId, scope);

        return ResponseEntity.created(URI.create(String.format("/%s/%s", "resources", scope.getId()))).build();
    }

    /**
     * Finds all {@link Scope} from a request.
     *
     * @param apiId       The Api Id
     * @param pageableDTO {@link PageableDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Scopes", responseContainer = "List", response = Scope.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_SCOPE)
    public ResponseEntity<?> findAll(@PathVariable("apiId") String apiId,
                                     @ModelAttribute PageableDTO pageableDTO) {

        if (!pageableDTO.isEmpty()) {
            Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit(), new Sort(Sort.Direction.ASC, "id"));

            Page<Scope> scopePage = scopeService.list(apiId, pageable);
            return ResponseEntity.ok(scopePage);
        } else {

            List<Scope> resources = scopeService.list(apiId);
            return ResponseEntity.ok(resources);
        }
    }

    /**
     * Updates a {@link Scope}.
     *
     * @param apiId    The Api Id
     * @param scopeId  The Scope Id
     * @param scopeDTO {@link ScopeDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Update Scope")
    @PutMapping(value = "/{scopeId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_SCOPE)
    public ResponseEntity<?> update(@PathVariable("apiId") String apiId,
                                    @PathVariable("scopeId") String scopeId,
                                    @RequestBody @Valid ScopeDTO scopeDTO) {

        Scope scope = GenericConverter.mapper(scopeDTO, Scope.class);

        scope = scopeService.update(apiId, scopeId, scope);

        return ResponseEntity.ok(scope);
    }

    /**
     * Deletes a {@link Scope}.
     *
     * @param apiId   The Api Id
     * @param scopeId The Scope Id
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Scope")
    @DeleteMapping(value = "/{scopeId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_SCOPE)
    public ResponseEntity<?> delete(@PathVariable("apiId") String apiId,
                                    @PathVariable("scopeId") String scopeId) {

        scopeService.delete(apiId, scopeId);

        return ResponseEntity.noContent().build();
    }

}
