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
import br.com.conductor.heimdall.core.dto.InterceptorDTO;
import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.core.entity.*;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.*;
import br.com.conductor.heimdall.core.service.amqp.AMQPInterceptorService;
import br.com.conductor.heimdall.core.util.ConstantsCache;
import br.com.conductor.heimdall.core.util.Pageable;
import br.com.conductor.heimdall.core.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * This class provides methods to create, read, update and delete a {@link Interceptor} resource.<br/>
 * This class also performs a validation  before it saves or deletes a {@link Interceptor}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 */
@Service
public class InterceptorService {

    @Autowired
    private InterceptorRepository interceptorRepository;

    @Autowired
    private PlanService planService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private RateLimitRepository ratelimitRepository;

    @Autowired
    private AMQPInterceptorService amqpInterceptorService;

    @Value("${zuul.filter.root}")
    private String zuulFilterRoot;

    /**
     * Finds a {@link Interceptor} by its ID.
     *
     * @param id The Id of the {@link Interceptor}
     * @return The {@link Interceptor} found
     */
    @Transactional(readOnly = true)
    public Interceptor find(String id) {

        Interceptor interceptor = interceptorRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(interceptor == null, GLOBAL_NOT_FOUND, "Interceptor");

        return interceptor;
    }

    /**
     * Generates a paged list of {@link Interceptor} from a request.
     *
     * @return The paged {@link Interceptor} list
     */
    @Transactional(readOnly = true)
    public Page<Interceptor> list(Pageable pageable) {

        return interceptorRepository.findAll(pageable);
    }

    /**
     * Generates a list of {@link Interceptor} from a request.
     *
     * @return The List<{@link Interceptor}> list
     */
    @Transactional(readOnly = true)
    public List<Interceptor> list() {

        return interceptorRepository.findAll();
    }

    /**
     * Saves a {@link Interceptor} to the repository.
     *
     * @param interceptor The {@link Interceptor}
     * @return The {@link Interceptor} saved
     */
    @Transactional
    public Interceptor save(final Interceptor interceptor) {

        updatesReferenceId(interceptor);

        validateTemplate(interceptor.getType(), interceptor.getContent());

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            HeimdallException.checkThrow(!InterceptorLifeCycle.API.equals(interceptor.getLifeCycle()), ExceptionMessage.CORS_INTERCEPTOR_NOT_API_LIFE_CYCLE);
            HeimdallException.checkThrow(apiService.find(interceptor.getApiId()).isCors(), ExceptionMessage.CORS_INTERCEPTOR_ALREADY_ASSIGNED_TO_THIS_API);
        }

        final Set<String> ignoredOperations = validateIgnoredOperations(interceptor.getIgnoredOperations());
        HeimdallException.checkThrow(!ignoredOperations.isEmpty(), INTERCEPTOR_IGNORED_INVALID, ignoredOperations.toString());

        HeimdallException.checkThrow(
                (TypeInterceptor.CLIENT_ID.equals(interceptor.getType()) && InterceptorLifeCycle.PLAN.equals(interceptor.getLifeCycle())),
                INTERCEPTOR_INVALID_LIFECYCLE);

        interceptor.setCreationDate(LocalDateTime.now());

        final Interceptor savedInterceptor = interceptorRepository.save(interceptor);

        if (TypeInterceptor.CORS.equals(savedInterceptor.getType())) {
            Api api = apiService.find(savedInterceptor.getApiId());
            api.setCors(true);
            apiService.update(api);
        }

        amqpInterceptorService.dispatchInterceptor(savedInterceptor.getId());

        return savedInterceptor;
    }

    private void validateTemplate(TypeInterceptor type, String content) {
        type.getHeimdallInterceptor().parseContent(content);
    }

    /**
     * Updates a {@link Interceptor} by its ID.
     *
     * @param id                 The ID of the {@link Interceptor} to be updated
     * @param interceptorPersist The {@link InterceptorDTO}
     * @return The updated {@link Interceptor}
     */
    public Interceptor update(String id, final Interceptor interceptorPersist) {

        final Interceptor interceptor = this.find(id);
        GenericConverter.mapper(interceptorPersist, interceptor);

        updatesReferenceId(interceptor);

        validateTemplate(interceptor.getType(), interceptor.getContent());

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            HeimdallException.checkThrow(!InterceptorLifeCycle.API.equals(interceptor.getLifeCycle()), ExceptionMessage.CORS_INTERCEPTOR_NOT_API_LIFE_CYCLE);
        }

        final Interceptor updatedInterceptor = interceptorRepository.save(interceptor);
        amqpInterceptorService.dispatchInterceptor(updatedInterceptor.getId());

        return updatedInterceptor;
    }

    /**
     * Deletes a{@link Interceptor} by its ID.
     *
     * @param id The Id of the {@link Interceptor} to be deleted
     */
    @Transactional
    public void delete(String id) {

        Interceptor interceptor = this.find(id);

        String fileName = StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId()) + ".groovy";
        String pathName = String.join(File.separator, zuulFilterRoot, interceptor.getExecutionPoint().getFilterType(), fileName);

        if (TypeInterceptor.RATTING == interceptor.getType()) {

            String path = ConstantsCache.RATE_LIMIT_KEY_PREFIX + interceptor.getId();

            ratelimitRepository.delete(path);
        }

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            final Api api = apiService.find(interceptor.getApiId());
            api.setCors(false);
            apiService.update(api);
        }

        interceptorRepository.delete(interceptor);

        amqpInterceptorService.dispatchRemoveInterceptors(new InterceptorFileDTO(interceptor.getId(), pathName));
    }

    /**
     * Deletes all Interceptors from a Operation
     *
     * @param operationId Operation with the attached Interceptors
     */
    @Transactional
    public void deleteAllfromOperation(String operationId) {
        this.deleteAll(interceptorRepository.findAllByOperationId(operationId));
    }

    /**
     * Deletes all Interceptors from a Resource
     *
     * @param resourceId Resource with the attached Interceptors
     */
    @Transactional
    public void deleteAllfromResource(String resourceId) {
        this.deleteAll(interceptorRepository.findAllByResourceId(resourceId));
    }

    /**
     * Deletes all Interceptors from a Api
     *
     * @param planId Plan with the attached Interceptors
     */
    @Transactional
    public void deleteAllFromPlan(String planId) {
        this.deleteAll(interceptorRepository.findAllByPlanId(planId));
    }

    /**
     * Deletes all Interceptors from a Api
     *
     * @param apiId Api with the attached Interceptors
     */
    @Transactional
    public void deleteAllFromApi(String apiId) {
        this.deleteAll(interceptorRepository.findAllByApiId(apiId));
    }

    private void deleteAll(List<Interceptor> interceptors) {
        interceptors.forEach(interceptor -> this.delete(interceptor.getId()));
    }

    /**
     * Private method to validate the lifecycle of a {@link Interceptor}.<br/>
     * Life cycle can be a PLAN, a RESOURCE or a OPERATION.
     *
     * @param interceptor The {@link Interceptor} to be validated
     * @return The validated {@link Interceptor}
     */
    private void updatesReferenceId(Interceptor interceptor) {

        interceptor.setResourceId(null);
        interceptor.setOperationId(null);
        interceptor.setPlanId(null);

        switch (interceptor.getLifeCycle()) {
            case PLAN:
                final Plan plan = planService.find(interceptor.getReferenceId());
                interceptor.setPlanId(plan.getId());
                break;
            case RESOURCE:
                final Resource resource = resourceService.find(interceptor.getReferenceId());
                interceptor.setResourceId(resource.getId());
                break;
            case OPERATION:
                final Operation operation = operationService.find(interceptor.getReferenceId());
                interceptor.setOperationId(operation.getId());
                break;
            default:
                break;
        }
    }

    private Set<String> validateIgnoredOperations(Set<String> ignoredList) {

        Set<String> invalids = new HashSet<>();
        if (ignoredList != null && !ignoredList.isEmpty()) {

            for (String ignored : ignoredList) {

                Object o = operationService.find(ignored);
                if (o == null) {
                    invalids.add(ignored);
                }
            }
        }

        return invalids;
    }

}
