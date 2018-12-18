
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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.ENVIRONMENT_INBOUND_DNS_PATTERN;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.ENVIRONMENT_ATTACHED_TO_API;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;
import static br.com.twsoftware.alfred.object.Objeto.notBlank;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.EnvironmentDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.EnvironmentPage;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.EnvironmentRepository;
import br.com.conductor.heimdall.core.util.Pageable;

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

    /**
     * Finds a {@link Environment} by its ID.
     *
     * @param id The id of the {@link Environment}
     * @return The {@link Environment} that was found
     * @throws NotFoundException Resource not found
     */
    public Environment find(Long id) {

        Environment environment = environmentRepository.findOne(id);
        HeimdallException.checkThrow(isBlank(environment), GLOBAL_RESOURCE_NOT_FOUND);

        return environment;
    }

    /**
     * Generates a paged list of {@link Environment} from a request.
     *
     * @param environmentDTO The {@link EnvironmentDTO}
     * @param pageableDTO    The {@link PageableDTO}
     * @return The paged {@link Environment} list as a {@link EnvironmentPage} object
     */
    public EnvironmentPage list(EnvironmentDTO environmentDTO, PageableDTO pageableDTO) {

        Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);

        Example<Environment> example = Example.of(environment, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
        Page<Environment> page = environmentRepository.findAll(example, pageable);

         return new EnvironmentPage(PageDTO.build(page));
    }

    /**
     * Generates a list of {@link Environment} from a request.
     *
     * @param environmentDTO The {@link EnvironmentDTO}
     * @return The List<{@link Environment}>
     */
    public List<Environment> list(EnvironmentDTO environmentDTO) {

        Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);

        Example<Environment> example = Example.of(environment, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

         return environmentRepository.findAll(example);
    }

    /**
     * Saves a {@link Environment} in the repository.
     *
     * @param environmentDTO The {@link EnvironmentDTO}
     * @throws BadRequestException Inbound URL already exists
     * @return The saved {@link Environment}
     */
    @Transactional
    public Environment save(EnvironmentDTO environmentDTO) {

        List<Environment> environments = environmentRepository.findByInboundURL(environmentDTO.getInboundURL());

        Environment environmentEqual = environments.stream().filter(e -> e.getOutboundURL().equals(environmentDTO.getOutboundURL())).findFirst().orElse(null);
        HeimdallException.checkThrow(notBlank(environmentEqual), ExceptionMessage.ENVIRONMENT_ALREADY_EXISTS);

        Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);
        HeimdallException.checkThrow(!validateInboundURL(environment.getInboundURL()), ENVIRONMENT_INBOUND_DNS_PATTERN);

        environment = environmentRepository.save(environment);

        return environment;
    }

    /**
     * Updates a {@link Environment} by its ID.
     *
     * @param id             The id of the {@link Environment}
     * @param environmentDTO The {@link EnvironmentDTO}
     * @throws NotFoundException   Resource not found
     * @throws BadRequestException Inbound URL already exists
     * @return The updated {@link Environment}
     */
    @Transactional
    public Environment update(Long id, EnvironmentDTO environmentDTO) {

        Environment environment = environmentRepository.findOne(id);
        HeimdallException.checkThrow(isBlank(environment), GLOBAL_RESOURCE_NOT_FOUND);

        List<Environment> environments = environmentRepository.findByInboundURL(environmentDTO.getInboundURL());

        Environment environmentEqual = environments.stream().filter(e -> e.getOutboundURL().equals(environmentDTO.getOutboundURL())).findFirst().orElse(null);
        HeimdallException.checkThrow(notBlank(environmentEqual) && !Objects.requireNonNull(environmentEqual).getId().equals(environment.getId()), ExceptionMessage.ENVIRONMENT_ALREADY_EXISTS);

        Integer apis = environmentRepository.findApiWithOtherEnvironmentEqualsInbound(environment.getId(), environmentDTO.getInboundURL());
        HeimdallException.checkThrow(apis > 0, ExceptionMessage.API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS);

        environment = GenericConverter.mapper(environmentDTO, environment);
        HeimdallException.checkThrow(!validateInboundURL(environment.getInboundURL()), ENVIRONMENT_INBOUND_DNS_PATTERN);

        environmentRepository.save(environment);

        return environment;
    }

     /**
      * Deletes a {@link Environment} by its ID.
      *
      * @param 	id 						The id of the {@link Environment}
      * @throws NotFoundException		Resource not found
      */
     @Transactional
     public void delete(Long id) {

        Environment environment = environmentRepository.findOne(id);
        HeimdallException.checkThrow(isBlank(environment), GLOBAL_RESOURCE_NOT_FOUND);

        Integer totalEnvironmentsAttached = environmentRepository.findApisWithEnvironment(id);
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
                return !temp.matches(".*/.*");
        }

        return false;
    }

}