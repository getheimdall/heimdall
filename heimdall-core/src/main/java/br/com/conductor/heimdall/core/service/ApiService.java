
package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.*;
import br.com.conductor.heimdall.core.dto.page.ApiPage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.Pageable;
import br.com.conductor.heimdall.core.util.StringUtils;
import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * This class provides methods to create, read, update and delete the {@link Api} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
@Slf4j
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private AMQPRouteService amqpRoute;

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private MiddlewareService middlewareService;

    @Autowired
    private SwaggerService swaggerService;

    /**
     * Finds a {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     * @return The {@link Api}
     */
    public Api find(Long id) {

        Api api = apiRepository.findOne(id);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        return api;
    }

    /**
     * Get Swagger from {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     * @return The {@link Api}
     */
    public Swagger findSwaggerByApi(Long id) {

        Api api = apiRepository.findOne(id);
        List<Resource> resources = resourceService.list(api.getId(), new ResourceDTO());
        api.setResources(new HashSet<>(resources));

        return swaggerService.exportApiToSwaggerJSON(api);
    }

    /**
     * Generates a paged list of the {@link Api}'s
     *
     * @param apiDTO      {@link ApiDTO}
     * @param pageableDTO {@link PageableDTO}
     * @return The paged {@link Api} list as a {@link ApiPage} object
     */
    public ApiPage list(ApiDTO apiDTO, PageableDTO pageableDTO) {

        Api api = GenericConverter.mapper(apiDTO, Api.class);

        Example<Api> example = Example.of(api, ExampleMatcher.matching().withIgnorePaths("cors").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
        Page<Api> page = apiRepository.findAll(example, pageable);

        ApiPage apiPage = new ApiPage(PageDTO.build(page));

        return apiPage;
    }

    /**
     * Generates a list of the {@link Api}'s
     *
     * @param apiDTO {@link ApiDTO}
     * @return The list of {@link Api}'s
     */
    public List<Api> list(ApiDTO apiDTO) {

        Api api = GenericConverter.mapper(apiDTO, Api.class);

        Example<Api> example = Example.of(api, ExampleMatcher.matching().withIgnorePaths("cors").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        List<Api> apis = apiRepository.findAll(example);

        apis.sort(Comparator.comparing(Api::getId));

        return apis;
     }

    /**
     * Saves a {@link Api}.
     *
     * @param apiDTO {@link ApiDTO}
     * @return The saved {@link Api}
     */
    public Api save(ApiDTO apiDTO) {

        Api validateApi = apiRepository.findByBasePath(apiDTO.getBasePath());
        HeimdallException.checkThrow(validateApi != null, API_BASEPATH_EXIST);
        HeimdallException.checkThrow(validateBasepath(apiDTO), API_BASEPATH_MALFORMED);
        HeimdallException.checkThrow(apiDTO.getBasePath() == null || apiDTO.getBasePath().isEmpty(), API_BASEPATH_EMPTY);
        HeimdallException.checkThrow(validateInboundsEnvironments(apiDTO.getEnvironments()), API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS);

          Api api = GenericConverter.mapper(apiDTO, Api.class);
          api.setBasePath(StringUtils.removeMultipleSlashes(api.getBasePath()));

        api = apiRepository.save(api);

        amqpRoute.dispatchRoutes();
        return api;
    }

    /**
     * Updates a {@link Api} by its ID.
     *
     * @param id     The ID of the {@link Api}
     * @param apiDTO {@link ApiDTO}
     * @return The updated {@link Api}
     */
    public Api update(Long id, ApiDTO apiDTO) {

        Api api = apiRepository.findOne(id);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        Api validateApi = apiRepository.findByBasePath(apiDTO.getBasePath());
        HeimdallException.checkThrow(validateApi != null && !Objects.equals(validateApi.getId(), api.getId()), API_BASEPATH_EXIST);
        HeimdallException.checkThrow(validateBasepath(apiDTO), API_BASEPATH_MALFORMED);
        HeimdallException.checkThrow(apiDTO.getBasePath() == null || apiDTO.getBasePath().isEmpty(), API_BASEPATH_EMPTY);
        HeimdallException.checkThrow(validateInboundsEnvironments(apiDTO.getEnvironments()), API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS);

          api = GenericConverter.mapper(apiDTO, api);
          api.setBasePath(StringUtils.removeMultipleSlashes(api.getBasePath()));

        api = apiRepository.save(api);

        amqpRoute.dispatchRoutes();
        return api;
    }

    /**
     * Updates a {@link Api} by Swagger JSON.
     *
     * @param id      The ID of the {@link Api}
     * @param swagger {@link JSONObject}
     * @return The updated {@link Api}
     */
    public Api updateBySwagger(Long id, String swagger, boolean override) {

        Api api = apiRepository.findOne(id);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        try {
            api = swaggerService.importApiFromSwaggerJSON(api, swagger, override);
        } catch (IOException e) {
            HeimdallException.checkThrow(api == null, GLOBAL_SWAGGER_JSON_INVALID_FORMAT);
        }

        api = apiRepository.save(api);

        amqpRoute.dispatchRoutes();
        return api;
    }

    /**
     * Deletes a {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     */
    public void delete(Long id) {

        Api api = apiRepository.findOne(id);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        resourceService.deleteAllFromApi(id);
        middlewareService.deleteAll(id);

        apiRepository.delete(api);
        amqpRoute.dispatchRoutes();
    }

     /**
      * Find plans from {@link Api} by its ID.
      *
      * @param id   The ID of the {@link Api}
      * @return     List of the {@link Plan}
      */
     @Transactional(readOnly = true)
     public List<Plan> plansByApi(Long id) {

          Api found = apiRepository.findOne(id);
          HeimdallException.checkThrow(Objects.isNull(found), API_NOT_EXIST);

          Hibernate.initialize(found.getPlans());
          return found.getPlans();
     }

    /*
     * A Api basepath can not have any sort of wild card.
     */
    private static boolean validateBasepath(ApiDTO apiDTO) {
        List<String> basepath = Arrays.asList(
                apiDTO.getBasePath()
                        .split("/")
        );

        return basepath.contains("*") || basepath.contains("**");
    }

    /**
     * Verify in environments if there are equal inbounds
     *
     * @param environmentsIds {@link List}<{@link ReferenceIdDTO}>
     * @return True if exist, false otherwise
     */
    private boolean validateInboundsEnvironments(List<ReferenceIdDTO> environmentsIds) {

        List<String> inbounds = environmentsIds.stream()
                .map(e -> environmentService.find(e.getId()))
                .filter(Objects::nonNull)
                .map(Environment::getInboundURL)
                .filter(e -> e != null && !e.isEmpty())
                .collect(Collectors.toList());

        return inbounds.stream().anyMatch(inbound -> Collections.frequency(inbounds, inbound) > 1);
    }
}
