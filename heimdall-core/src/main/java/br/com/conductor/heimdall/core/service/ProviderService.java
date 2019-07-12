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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.ProviderDTO;
import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.entity.ProviderParam;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides methos to create, read, update and delete a {@link Provider}
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    /**
     * Saves a {@link Provider} to the repository
     *
     * @param provider The {@link Provider}
     * @return The saved {@link Provider}
     */
    public Provider save(Provider provider) {
        provider.setProviderDefault(false);
        provider.getProviderParams().forEach(p -> p.setProvider(provider));
        provider.setCreationDate(LocalDateTime.now());
        return this.providerRepository.save(provider);
    }

    /**
     * Edits a {@link Provider} by its Id
     *
     * @param id           The {@link Provider} Id
     * @param providerEdit The {@link ProviderDTO}
     * @return The edited {@link Provider}
     */
    @Transactional
    public Provider edit(String id, Provider providerEdit) {
        Provider found = this.find(id);

        HeimdallException.checkThrow(found.isProviderDefault(), ExceptionMessage.DEFAULT_PROVIDER_CAN_NOT_UPDATED_OR_REMOVED);

        found.setName(providerEdit.getName());
        found.setPath(providerEdit.getPath());
        if (Objects.nonNull(providerEdit.getDescription())) {
            found.setDescription(providerEdit.getDescription());
        }

        List<ProviderParam> providers = providerEdit.getProviderParams().stream()
                .map(providerParams -> getProviderParam(providerParams, found)).collect(Collectors.toList());

        found.getProviderParams().clear();
        found.getProviderParams().addAll(providers);

        return this.providerRepository.save(found);
    }

    /**
     * Generates a paged list of {@link Provider} from a request
     *
     * @param pageable The {@link Pageable}
     * @return The paged {@link Provider} list
     */
    public Page<Provider> list(Pageable pageable) {

        return this.providerRepository.findAll(pageable);
    }

    /**
     * Generates a list of {@link Provider} from a request
     *
     * @return The list of {@link Provider}
     */
    public List<Provider> list() {

        return this.providerRepository.findAll();
    }

    /**
     * Finds a {@link Provider} by its Id
     *
     * @param id The {@link Provider} Id
     * @return The {@link Provider}
     */
    public Provider find(String id) {

        Provider provider = providerRepository.findById(id).orElse(null);

        HeimdallException.checkThrow(Objects.isNull(provider), ExceptionMessage.GLOBAL_NOT_FOUND, "Provider");

        return provider;
    }

    /**
     * Deletes a {@link Provider} by its Id
     *
     * @param id The {@link Provider} Id
     */
    public void delete(String id) {
        final Provider provider = this.find(id);

        this.providerRepository.delete(provider);
    }

    protected ProviderParam getProviderParam(ProviderParam p, Provider provider) {
        ProviderParam providerParam = new ProviderParam();
        providerParam.setProvider(provider);
        providerParam.setValue(p.getValue());
        providerParam.setLocation(p.getLocation());
        providerParam.setName(p.getName());
        return providerParam;
    }
}
