package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ProviderDTO;
import br.com.conductor.heimdall.core.dto.page.ProviderPage;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.repository.ProviderRepository;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * This class provides methos to create, read, update and delete a {@link Provider}
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    /**
     * Saves a {@link Provider} to the repository
     *
     * @param providerPersist The {@link ProviderDTO}
     * @return The saved {@link Provider}
     */
    public Provider save(ProviderDTO providerPersist) {
        Provider provider = GenericConverter.mapper(providerPersist, Provider.class);
        return this.providerRepository.save(provider);
    }

    /**
     * Edits a {@link Provider} by its Id
     *
     * @param idProvider   The {@link Provider} Id
     * @param providerEdit The {@link ProviderDTO}
     * @return The edited {@link Provider}
     */
    public Provider edit(Long idProvider, ProviderDTO providerEdit) {
        Provider found = this.providerRepository.findOne(idProvider);
        Provider provider = GenericConverter.mapper(providerEdit, found);
        return this.providerRepository.save(provider);
    }

    /**
     * Generates a paged list of {@link Provider} from a request
     *
     * @param providerDTO The {@link ProviderDTO}
     * @param pageableDTO The {@link PageableDTO}
     * @return The paged {@link Provider} list as a {@link ProviderPage} object
     */
    public ProviderPage listWithPageableAndFilter(ProviderDTO providerDTO, PageableDTO pageableDTO) {

        Provider provider = GenericConverter.mapper(providerDTO, Provider.class);
        Example<Provider> example = Example.of(provider,
                ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());

        Page<Provider> page = this.providerRepository.findAll(example, pageable);

        return new ProviderPage(PageDTO.build(page));
    }

    /**
     * Generates a list of {@link Provider} from a request
     *
     * @param providerDTO The {@link ProviderDTO}
     * @return The list of {@link Provider}
     */
    public List<Provider> listWithFilter(ProviderDTO providerDTO) {

        Provider provider = GenericConverter.mapper(providerDTO, Provider.class);
        Example<Provider> example = Example.of(provider,
                ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        return this.providerRepository.findAll(example);
    }

    /**
     * Finds a {@link Provider} by its Id
     *
     * @param id The {@link Provider} Id
     * @return The {@link Provider}
     */
    public Provider findOne(Long id) {
        return this.providerRepository.findOne(id);
    }

    /**
     * Deletes a {@link Provider} by its Id
     *
     * @param id The {@link Provider} Id
     */
    public void delete(Long id) {
        this.providerRepository.delete(id);
    }
}
