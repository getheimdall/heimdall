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
package br.com.conductor.heimdall.core.service;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.INTERCEPTOR_IGNORED_INVALID;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.INTERCEPTOR_INVALID_LIFECYCLE;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.INTERCEPTOR_REFERENCE_NOT_FOUND;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.InterceptorDTO;
import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.interceptor.RateLimitDTO;
import br.com.conductor.heimdall.core.dto.page.InterceptorPage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.interceptor.impl.RattingHeimdallInterceptor;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.repository.RateLimitRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPInterceptorService;
import br.com.conductor.heimdall.core.util.ConstantsCache;
import br.com.conductor.heimdall.core.util.Pageable;
import br.com.conductor.heimdall.core.util.StringUtils;

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
    private PlanRepository planRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private ApiRepository apiRepository;

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

        Interceptor interceptor = interceptorRepository.findOne(id);
        HeimdallException.checkThrow(interceptor == null, GLOBAL_RESOURCE_NOT_FOUND);

        return interceptor;
    }

    /**
     * Generates a paged list of {@link Interceptor} from a request.
     *
     * @param interceptorDTO The {@link InterceptorDTO}
     * @param pageableDTO    The {@link PageableDTO}
     * @return The paged {@link Interceptor} list as a {@link InterceptorPage} object
     */
    @Transactional(readOnly = true)
    public InterceptorPage list(InterceptorDTO interceptorDTO, PageableDTO pageableDTO) {

        Interceptor interceptor = GenericConverter.mapper(interceptorDTO, Interceptor.class);

        Example<Interceptor> example = Example.of(interceptor, ExampleMatcher.matching().withIgnorePaths("api.cors").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
        Page<Interceptor> page = interceptorRepository.findAll(example, pageable);

        return new InterceptorPage(PageDTO.build(page));
    }

    /**
     * Generates a list of {@link Interceptor} from a request.
     *
     * @param interceptorDTO The {@link InterceptorDTO}
     * @return The List<{@link Interceptor}> list
     */
    @Transactional(readOnly = true)
    public List<Interceptor> list(InterceptorDTO interceptorDTO) {

        Interceptor interceptor = GenericConverter.mapper(interceptorDTO, Interceptor.class);

        Example<Interceptor> example = Example.of(interceptor, ExampleMatcher.matching().withIgnorePaths("api.cors").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

        return interceptorRepository.findAll(example);
    }

    /**
     * Saves a {@link Interceptor} to the repository.
     *
     * @param interceptorDTO The {@link InterceptorDTO}
     * @return The {@link Interceptor} saved
     */
    @Transactional
    public Interceptor save(InterceptorDTO interceptorDTO) {

        Interceptor interceptor = GenericConverter.mapper(interceptorDTO, Interceptor.class);

        interceptor = validateLifeCycle(interceptor);

        validateTemplate(interceptor.getType(), interceptor.getContent());

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            validateFilterCors(interceptor);
        }

//        List<String> ignoredResources = ignoredValidate(interceptorDTO.getIgnoredResources(), resourceRepository);
//        HeimdallException.checkThrow(!ignoredResources.isEmpty(), INTERCEPTOR_IGNORED_INVALID, ignoredResources.toString());
//
//        List<String> ignoredOperations = ignoredValidate(interceptorDTO.getIgnoredOperations(), operationRepository);
//        HeimdallException.checkThrow(!ignoredOperations.isEmpty(), INTERCEPTOR_IGNORED_INVALID, ignoredOperations.toString());

        HeimdallException.checkThrow((TypeInterceptor.CLIENT_ID.equals(interceptor.getType()) && InterceptorLifeCycle.PLAN.equals(interceptor.getLifeCycle())), INTERCEPTOR_INVALID_LIFECYCLE, interceptor.getType().name());

        interceptor = interceptorRepository.save(interceptor);

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            Api api = apiService.find(interceptor.getApi().getId());
            api.setCors(true);
            apiService.update(api.getId(), api);
        }

        amqpInterceptorService.dispatchInterceptor(interceptor.getId());

        return interceptor;
    }

    private void validateTemplate(TypeInterceptor type, String content) {
        type.getHeimdallInterceptor().parseContent(content);
    }

    /**
     * Updates a {@link Interceptor} by its ID.
     *
     * @param id             The ID of the {@link Interceptor} to be updated
     * @param interceptorDTO The {@link InterceptorDTO}
     * @return The updated {@link Interceptor}
     */
    public Interceptor update(String id, InterceptorDTO interceptorDTO) {

        Interceptor interceptor = this.find(id);
        HeimdallException.checkThrow(interceptor == null, GLOBAL_RESOURCE_NOT_FOUND);
        interceptor = GenericConverter.mapper(interceptorDTO, interceptor);

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            HeimdallException.checkThrow(interceptor.getLifeCycle() != InterceptorLifeCycle.API, ExceptionMessage.CORS_INTERCEPTOR_NOT_API_LIFE_CYCLE);
        }

        interceptor = validateLifeCycle(interceptor);

        validateTemplate(interceptor.getType(), interceptor.getContent());

        if (TypeInterceptor.RATTING == interceptor.getType()) {
            RateLimitDTO rateLimitDTO = new RattingHeimdallInterceptor().parseContent(interceptor.getContent());
            ratelimitRepository.mountRatelimit(interceptor.getId(), rateLimitDTO.getCalls(), rateLimitDTO.getInterval());
        }

        interceptor = interceptorRepository.save(interceptor);
        amqpInterceptorService.dispatchInterceptor(interceptor.getId());

        return interceptor;
    }

    /**
     * Deletes a{@link Interceptor} by its ID.
     *
     * @param id The Id of the {@link Interceptor} to be deleted
     */
    @Transactional
    public void delete(String id) {

        Interceptor interceptor = this.find(id);
        HeimdallException.checkThrow(interceptor == null, GLOBAL_RESOURCE_NOT_FOUND);

        String fileName = StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId().toString()) + ".groovy";
        String pathName = String.join(File.separator, zuulFilterRoot, interceptor.getExecutionPoint().getFilterType(), fileName);

        if (TypeInterceptor.RATTING == interceptor.getType()) {

            String path = ConstantsCache.RATE_LIMIT_KEY_PREFIX + interceptor.getId();

            ratelimitRepository.delete(path);
        }

        if (TypeInterceptor.CORS.equals(interceptor.getType())) {
            interceptor.getApi().setCors(false);
            apiService.update(interceptor.getApi().getId(), interceptor.getApi());
        }

        interceptorRepository.delete(interceptor);

        amqpInterceptorService.dispatchRemoveInterceptors(new InterceptorFileDTO(interceptor.getId(), pathName));
    }

    /**
     * Deletes all Interceptors from a Operation
     *
     * @param operationId Operation with the attatched Interceptors
     */
    @Transactional
    public void deleteAllfromOperation(String operationId) {
        List<Interceptor> interceptors = interceptorRepository.findByOperationId(operationId);
        interceptors.forEach(interceptor -> this.delete(interceptor.getId()));
    }
    /**
     * Deletes all Interceptors from a Resource
     *
     * @param resourceId Resource with the attatched Interceptors
     */
    @Transactional
    public void deleteAllfromResource(String resourceId) {
        List<Interceptor> interceptors = interceptorRepository.findByResourceId(resourceId);
        interceptors.forEach(interceptor -> this.delete(interceptor.getId()));
    }

    /**
     * Private method to validate the lifecycle of a {@link Interceptor}.<br/>
     * Life cycle can be a PLAN, a RESOURCE or a OPERATION.
     *
     * @param interceptor The {@link Interceptor} to be validated
     * @return The validated {@link Interceptor}
     */
    private Interceptor validateLifeCycle(Interceptor interceptor) {

        switch (interceptor.getLifeCycle()) {
            case API:
                Api api = apiRepository.findOne(interceptor.getReferenceId());
                HeimdallException.checkThrow(api == null, INTERCEPTOR_REFERENCE_NOT_FOUND);
                interceptor.setResource(null);
                interceptor.setOperation(null);
                interceptor.setPlan(null);
                interceptor.setApi(api);
                break;
            case PLAN:
                Plan plan = planRepository.findOne(interceptor.getReferenceId());
                HeimdallException.checkThrow(plan == null, INTERCEPTOR_REFERENCE_NOT_FOUND);
                interceptor.setResource(null);
                interceptor.setOperation(null);
                interceptor.setPlan(plan);
                interceptor.setApi(plan.getApi());
                break;
            case RESOURCE:
                Resource resource = resourceRepository.findOne(interceptor.getReferenceId());
                HeimdallException.checkThrow(resource == null, INTERCEPTOR_REFERENCE_NOT_FOUND);
                interceptor.setOperation(null);
                interceptor.setPlan(null);
                interceptor.setResource(resource);
                interceptor.setApi(resource.getApi());
                break;
            case OPERATION:
                Operation operation = operationRepository.findOne(interceptor.getReferenceId());
                HeimdallException.checkThrow(operation == null, INTERCEPTOR_REFERENCE_NOT_FOUND);
                interceptor.setResource(null);
                interceptor.setPlan(null);
                interceptor.setOperation(operation);
                interceptor.setApi(operation.getResource().getApi());
                break;
            default:
                break;
        }

        return interceptor;
    }

    private void validateFilterCors(Interceptor interceptor) {
        HeimdallException.checkThrow(interceptor.getLifeCycle() != InterceptorLifeCycle.API, ExceptionMessage.CORS_INTERCEPTOR_NOT_API_LIFE_CYCLE);
        HeimdallException.checkThrow(interceptor.getApi().isCors(), ExceptionMessage.CORS_INTERCEPTOR_ALREADY_ASSIGNED_TO_THIS_API);
    }

//    private List<String> ignoredValidate(List<String> ignoredList, JpaRepository<?, String> repository) {
//
//        List<String> invalids = new ArrayList<>();
//        if (ignoredList != null && !ignoredList.isEmpty()) {
//
//            for (String ignored : ignoredList) {
//
//                Object o = repository.find(ignored);
//                if (o == null) {
//                    invalids.add(ignored);
//                }
//            }
//        }
//
//        return invalids;
//    }

}
