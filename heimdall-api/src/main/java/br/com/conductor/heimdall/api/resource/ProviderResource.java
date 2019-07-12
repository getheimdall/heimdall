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
import br.com.conductor.heimdall.core.dto.ProviderDTO;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.service.ProviderService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_PROVIDER;

/**
 * Uses a {@link ProviderService} to provide methods to create, read, update and delete a {@link Provider}
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@io.swagger.annotations.Api(
        value = PATH_PROVIDER,
        produces = MediaType.APPLICATION_JSON_VALUE,
        tags = {ConstantsTag.TAG_PROVIDERS})
@RestController
@RequestMapping(PATH_PROVIDER)
public class ProviderResource {

    @Autowired
    private ProviderService providerService;

    /**
     * Save a new {@link Provider}.
     *
     * @param providerPersist The {@link ProviderDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Save a new provider")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_CREATE_PROVIDER)
    public ResponseEntity<?> save(@RequestBody ProviderDTO providerPersist) {

        Provider provider = GenericConverter.mapper(providerPersist, Provider.class);

        Provider saved = this.providerService.save(provider);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Finds all {@link Provider} from a request.
     *
     * @param pageableDTO {@link PageableDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Providers with filter and pageable", responseContainer = "List", response = Provider.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PROVIDER)
    public ResponseEntity<?> findAll(@ModelAttribute PageableDTO pageableDTO) {

        if (!pageableDTO.isEmpty()) {
            Pageable pageable = PageRequest.of(pageableDTO.getPage(), pageableDTO.getLimit());
            Page<Provider> providerPage = providerService.list(pageable);

            return ResponseEntity.ok(providerPage);
        } else {
            List<Provider> listProviders = providerService.list();

            return ResponseEntity.ok(listProviders);
        }
    }

    /**
     * Finds {@link Provider} by its Id
     *
     * @param id The {@link Provider} Id
     * @return THe {@link Provider}
     */
    @ResponseBody
    @ApiOperation(value = "Find Provider by ID", response = Provider.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {

        Provider provider = providerService.find(id);

        return ResponseEntity.ok(provider);
    }

    /**
     * Updates a {@link Provider} by its Id
     *
     * @param idProvider  The {@link Provider} Id
     * @param providerDTO The {@link ProviderDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Update provider")
    @PutMapping(value = "/{idProvider}")
    public ResponseEntity<?> update(@PathVariable String idProvider,
                                    @RequestBody ProviderDTO providerDTO) {
        Provider provider = GenericConverter.mapper(providerDTO, Provider.class);

        Provider providerEdit = this.providerService.edit(idProvider, provider);

        return ResponseEntity.ok(providerEdit);
    }

    /**
     * Deletes a {@link Provider} by its Id
     *
     * @param providerId The {@link Provider} Id
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Delete Provider")
    @DeleteMapping(value = "/{providerId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_DELETE_OPERATION)
    public ResponseEntity<?> delete(@PathVariable String providerId) {

        this.providerService.delete(providerId);

        return ResponseEntity.noContent().build();
    }

}
