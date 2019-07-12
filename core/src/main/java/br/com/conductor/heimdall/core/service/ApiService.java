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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.API_BASEPATH_EMPTY;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.API_BASEPATH_MALFORMED;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_ALREADY_REGISTERED;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_SWAGGER_JSON_INVALID_FORMAT;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.exception.HeimdallException;
//import br.com.conductor.heimdall.core.publisher.AMQPRouteService;
import br.com.conductor.heimdall.core.publisher.RedisRoutePublisher;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.StringUtils;
import io.swagger.models.Swagger;

/**
 * This class provides methods to create, read, update and delete the {@link Api} resource.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class ApiService {

    private final RedisRoutePublisher redisRoutePublisher;
    private final ApiRepository apiRepository;
    private final EnvironmentService environmentService;
    private final InterceptorService interceptorService;
    private final PlanService planService;
    private final ResourceService resourceService;
    private final SwaggerService swaggerService;

    public ApiService(RedisRoutePublisher redisRoutePublisher,
                      ApiRepository apiRepository,
                      EnvironmentService environmentService,
                      InterceptorService interceptorService,
                      PlanService planService,
                      ResourceService resourceService,
                      SwaggerService swaggerService) {
        this.redisRoutePublisher = redisRoutePublisher;
        this.apiRepository = apiRepository;
        this.environmentService = environmentService;
        this.interceptorService = interceptorService;
        this.planService = planService;
        this.resourceService = resourceService;
        this.swaggerService = swaggerService;
    }

    /**
     * Finds a {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     * @return The {@link Api}
     */
    public Api find(String id) {

        Api api = apiRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(api == null, GLOBAL_NOT_FOUND, "Api");

        return api;
    }

    /**
     * Get Swagger from {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     * @return The {@link Api}
     */
    public Swagger findSwaggerByApi(String id) {

        Api api = this.find(id);
        List<Resource> resources = resourceService.list(api.getId());
        api.setResources(resources.stream().map(Resource::getId).collect(Collectors.toSet()));

        return swaggerService.exportApiToSwaggerJSON(api);
    }

    /**
     * Generates a paged list of the {@link Api}'s
     *
     * @param pageable {@link Pageable}
     * @return The paged {@link Api} list
     */
    public Page<Api> list(Pageable pageable) {

        final List<Api> apis = this.list();

        return new PageImpl<>(apis, pageable, apis.size());
    }

    /**
     * Generates a list of the {@link Api}'s
     *
     * @return The list of {@link Api}'s
     */
    public List<Api> list() {

        List<Api> apis = apiRepository.findAll();

        apis.sort(Comparator.comparing(Api::getName));

        return apis;
    }

    /**
     * Saves a {@link Api}.
     *
     * @param api {@link Api}
     * @return The saved {@link Api}
     */
    public Api save(final Api api) {

        HeimdallException.checkThrow(api.getBasePath() == null || api.getBasePath().isEmpty(), API_BASEPATH_EMPTY);
        HeimdallException.checkThrow(this.checkWildCardsInBasepath(api.getBasePath()), API_BASEPATH_MALFORMED);

        String basepath = StringUtils.removeMultipleSlashes(api.getBasePath());
        if (basepath.endsWith(ConstantsPath.PATH_ROOT)) {
            basepath = basepath.substring(0, basepath.length() - 1);
        }

        HeimdallException.checkThrow(apiRepository.findByBasePath(basepath) != null, GLOBAL_ALREADY_REGISTERED, "Api basepath");
        HeimdallException.checkThrow(validateInboundsEnvironments(api), API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS);

        api.setBasePath(basepath);
        api.setCreationDate(LocalDateTime.now());

        final Api savedApi = apiRepository.save(api);

        redisRoutePublisher.dispatchRoutes();
        return savedApi;
    }

    /**
     * Updates a {@link Api} by its ID.
     *
     * @param id         The ID of the {@link Api}
     * @param apiPersist {@link Api}
     * @return The updated {@link Api}
     */
    public Api update(String id, Api apiPersist) {

        final Api api = this.find(id);

        HeimdallException.checkThrow(apiPersist.getBasePath() == null || apiPersist.getBasePath().isEmpty(), API_BASEPATH_EMPTY);
        HeimdallException.checkThrow(checkWildCardsInBasepath(apiPersist.getBasePath()), API_BASEPATH_MALFORMED);

        final Api validateApi = apiRepository.findByBasePath(apiPersist.getBasePath());
        HeimdallException.checkThrow(validateApi != null && !Objects.equals(validateApi.getId(), api.getId()), GLOBAL_ALREADY_REGISTERED, "Api basepath");

        final Api updatedApi = GenericConverter.mapper(apiPersist, api);
        updatedApi.setBasePath(StringUtils.removeMultipleSlashes(api.getBasePath()));

        HeimdallException.checkThrow(validateInboundsEnvironments(updatedApi), API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS);

        final Api savedApi = apiRepository.save(updatedApi);

        redisRoutePublisher.dispatchRoutes();
        return savedApi;
    }

    public Api update(Api api) {
        return this.update(api.getId(), api);
    }

    /**
     * Updates a {@link Api} by Swagger JSON.
     *
     * @param id      The ID of the {@link Api}
     * @param swagger {@link JSONObject}
     * @return The updated {@link Api}
     */
    public Api updateBySwagger(String id, String swagger, boolean override) {

        Api api = this.find(id);

        try {
            api = swaggerService.importApiFromSwaggerJSON(api, swagger, override);
        } catch (IOException e) {
            HeimdallException.checkThrow(api == null, GLOBAL_SWAGGER_JSON_INVALID_FORMAT);
        }

        api = apiRepository.save(api);

        redisRoutePublisher.dispatchRoutes();

        return api;
    }

    /**
     * Deletes a {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     */
    public void delete(String id) {

        Api api = this.find(id);

        resourceService.deleteAllFromApi(id);

        interceptorService.deleteAllFromApi(id);

        apiRepository.delete(api);
        redisRoutePublisher.dispatchRoutes();
    }

    /**
     * Find plans from {@link Api} by its ID.
     *
     * @param id The ID of the {@link Api}
     * @return List of the {@link Plan}
     */
    @Transactional(readOnly = true)
    public Set<Plan> plansByApi(String id) {

        Api found = this.find(id);

        return found.getPlans().stream().map(planService::find).collect(Collectors.toSet());
    }

    /*
     * A Api basepath can not have any sort of wild card.
     */
    private boolean checkWildCardsInBasepath(String basepath) {
        List<String> basepathArray = Arrays.asList(
                basepath.split("/")
        );

        return basepathArray.contains("*") || basepathArray.contains("**");
    }

    /**
     * Verify in environments if there are equal inbounds
     *
     * @param api {@link Api}
     * @return True if exist, false otherwise
     */
    private boolean validateInboundsEnvironments(Api api) {
        final List<Environment> environments = api.getEnvironments().stream()
                .map(environmentService::find)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> inbounds = environments.stream()
                .map(Environment::getInboundURL)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return inbounds.stream().anyMatch(inbound -> Collections.frequency(inbounds, inbound) > 1);
    }

    public void removePlan(Plan plan) {
        Api api = this.find(plan.getApiId());

        api.removePlan(plan.getId());

        this.update(api);
    }

    public void removeResource(Resource resource) {
        Api api = this.find(resource.getApiId());

        api.removeResource(resource.getId());

        this.update(api);
    }

}
