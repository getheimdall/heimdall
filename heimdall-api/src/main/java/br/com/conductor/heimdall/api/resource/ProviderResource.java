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

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_PROVIDER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import br.com.conductor.heimdall.core.dto.ProviderDTO;
import br.com.conductor.heimdall.core.dto.page.ProviderPage;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.service.ProviderService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import br.com.twsoftware.alfred.object.Objeto;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a {@link ProviderService} to provide methods to create, read, update and delete a {@link Provider}
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@io.swagger.annotations.Api(value = PATH_PROVIDER, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_PROVIDERS})
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
        Provider saved = this.providerService.save(providerPersist);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    /**
     * Finds all {@link Provider} from a request.
     *
     * @param providerDTO {@link ProviderDTO}
     * @param pageableDTO {@link PageableDTO}
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find all Providers with filter and pageable", responseContainer = "List", response = Provider.class)
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_PROVIDER)
    public ResponseEntity<?> findAll(@ModelAttribute ProviderDTO providerDTO, @ModelAttribute PageableDTO pageableDTO) {

        if (Objeto.notBlank(pageableDTO)) {

            ProviderPage providerPage = providerService.listWithPageableAndFilter(providerDTO, pageableDTO);
            return ResponseEntity.ok(providerPage);
        } else {
            List<Provider> listProviders = providerService.listWithFilter(providerDTO);
            return ResponseEntity.ok(listProviders);
        }
    }

    /**
     * Finds {@link Provider} by its Id
     *
     * @param id    The {@link Provider} Id
     * @return      THe {@link Provider}
     */
    @ResponseBody
    @ApiOperation(value = "Find Provider by ID", response = Provider.class)
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {

        Provider provider = providerService.findOne(id);

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
    public ResponseEntity<?> update(@PathVariable Long idProvider, @RequestBody ProviderDTO providerDTO) {
        Provider providerEdit = this.providerService.edit(idProvider, providerDTO);

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
    public ResponseEntity<?> delete(@PathVariable Long providerId) {

        this.providerService.delete(providerId);

        return ResponseEntity.noContent().build();
    }
}
