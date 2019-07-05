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

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.EnvironmentRepository;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * This class provides methods to create, read, update and delete the {@link Environment} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class EnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private ApiService apiService;

    /**
     * Finds a {@link Environment} by its ID.
     *
     * @param id The id of the {@link Environment}
     * @return The {@link Environment} that was found
     */
    public Environment find(String id) {

        Environment environment = environmentRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(environment == null, GLOBAL_NOT_FOUND, "Environment");

        return environment;
    }

    /**
     * Generates a paged list of {@link Environment} from a request.
     *
     * @param pageable The {@link Pageable}
     * @return The paged {@link Environment} list
     */
    public Page<Environment> list(Pageable pageable) {

        return environmentRepository.findAll(pageable);
    }

    /**
     * Generates a list of {@link Environment} from a request.
     *
     * @return The List<{@link Environment}>
     */
    public List<Environment> list() {

        return environmentRepository.findAll();
    }

    /**
     * Saves a {@link Environment} in the repository.
     *
     * @param environment The {@link Environment}
     * @return The saved {@link Environment}
     */
    @Transactional
    public Environment save(Environment environment) {

        List<Environment> environments = environmentRepository.findByInboundURL(environment.getInboundURL());

        Environment environmentEqual = environments.stream().filter(e -> e.getOutboundURL().equals(environment.getOutboundURL())).findFirst().orElse(null);
        HeimdallException.checkThrow(environmentEqual != null, ENVIRONMENT_ALREADY_EXISTS);
        HeimdallException.checkThrow(validateInboundURL(environment.getInboundURL()), ENVIRONMENT_INBOUND_DNS_PATTERN);

        environment.setCreationDate(LocalDateTime.now());

        return environmentRepository.save(environment);
    }

    /**
     * Updates a {@link Environment} by its ID.
     *
     * @param id                 The id of the {@link Environment}
     * @param environmentPersist The {@link Environment}
     * @return The updated {@link Environment}
     */
    @Transactional
    public Environment update(String id, Environment environmentPersist) {

        Environment environment = this.find(id);

        HeimdallException.checkThrow(validateInboundURL(environment.getInboundURL()), ENVIRONMENT_INBOUND_DNS_PATTERN);

        List<Environment> environments = environmentRepository.findByInboundURL(environmentPersist.getInboundURL());

        Environment environmentEqual = environments.stream().filter(e -> e.getOutboundURL().equals(environmentPersist.getOutboundURL())).findFirst().orElse(null);
        HeimdallException.checkThrow(environmentEqual != null && !Objects.requireNonNull(environmentEqual).getId().equals(environment.getId()), ENVIRONMENT_ALREADY_EXISTS);

        this.getApisWithEnvironment(id)
                .forEach(api -> {
                    final long count = api.getEnvironments().stream()
                        .map(this::find)
                        .filter(env -> env.getInboundURL().equals(environmentPersist.getInboundURL()))
                        .count()
                        ;

                    HeimdallException.checkThrow(count > 1, ExceptionMessage.API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS);
                });

        environment = GenericConverter.mapper(environmentPersist, environment);

        return environmentRepository.save(environment);
    }

    /**
     * Deletes a {@link Environment} by its ID.
     *
     * @param id The id of the {@link Environment}
     */
    @Transactional
    public void delete(String id) {

        Environment environment = this.find(id);

        final long totalEnvironmentsAttached = this.getApisWithEnvironment(id).size();
//        Integer totalEnvironmentsAttached = environmentRepository.findApisWithEnvironment(id);
        HeimdallException.checkThrow(totalEnvironmentsAttached > 0, ENVIRONMENT_ATTACHED_TO_API);

        environmentRepository.delete(environment);
    }

    /*
     * Validates if the String follow one of the patterns:
     *     * http[s]://host.domain[:port]
     *     * www.host.domain[:port]
     *
     * @return true if it is a a valid inbound, false otherwise
     */
    private boolean validateInboundURL(String inbound) {

        if (inbound.matches("(http://|https://|www\\.)(.+)")) {
            String temp = inbound.replaceAll("(http://|https://|www\\.)(.+)", "$2");

            if (temp.matches(".+\\..+"))
                return temp.matches(".*/.*");
        }

        return true;
    }

    private List<Api> getApisWithEnvironment(String environmentId) {
        return apiService.list().stream()
                .filter(api -> api.getEnvironments().contains(environmentId))
                .collect(Collectors.toList());
    }
}