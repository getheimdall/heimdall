
package br.com.conductor.heimdall.gateway.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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

import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.core.dto.interceptor.AccessTokenClientIdDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.core.util.*;
import br.com.twsoftware.alfred.object.Objeto;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.filters.FilterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.com.conductor.heimdall.core.util.Constants.MIDDLEWARE_API_ROOT;

/**
 * Provides methods to create and remove the {@link Interceptor} files.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
@Slf4j
public class InterceptorFileService {

    @Autowired
    private InterceptorRepository interceptorRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Value("${zuul.filter.root}")
    private String zuulFilterRoot;

    /**
     * Creates a {@link Interceptor} from its Id.
     *
     * @param id The {@link Interceptor} Id
     */
    @Transactional(readOnly = true)
    public void createFileInterceptor(Long id) {

        try {

            Interceptor interceptor = interceptorRepository.findOne(id);
            HeimdallException.checkThrow(Objeto.isBlank(interceptor), ExceptionMessage.INTERCEPTOR_NOT_EXIST);

            File file = templateInterceptor(interceptor.getType(), interceptor.getExecutionPoint());

            String template = Files.toString(file, Charsets.UTF_8);
            generateFileInterceptor(interceptor, template, buildParametersFile(interceptor, file));

        } catch (IOException e) {

            log.error(e.getMessage(), e);
        }

    }

    /*
     * Constructs a parameter file from a Interceptor.
     */
    private HashMap<String, Object> buildParametersFile(Interceptor interceptor, File file) {

        Long INVALID_REFERENCE_ID = -1L;

        if (Objeto.notBlank(file)) {

            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("order", StringUtils.generateOrder(definePrefixOrder(interceptor.getLifeCycle()), interceptor.getOrder()));
            parameters.put("executionPoint", interceptor.getExecutionPoint().getFilterType());
            parameters.put("pathsAllowed", pathsAllowed(interceptor));
            parameters.put("pathsNotAllowed", pathsNotAllowed(interceptor));
            parameters.put("lifeCycle", interceptor.getLifeCycle().name());
            parameters.put("name", StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId().toString()));
            parameters.put("zuulFilterRoot", zuulFilterRoot);
            parameters.put("path", createPath(interceptor));

            if (interceptor.getReferenceId() != null)
                parameters.put("referenceId", interceptor.getReferenceId());
            else
                parameters.put("referenceId", INVALID_REFERENCE_ID);

            if (Objeto.notBlank(interceptor.getOperation())) {

                parameters.put("method", interceptor.getOperation().getMethod().name());
            }

            if (Objeto.notBlank(interceptor.getEnvironment())) {

                parameters.put("inboundURL", interceptor.getEnvironment().getInboundURL());
            } else {

                parameters.put("inboundURL", null);
            }

            return buildCustom(parameters, interceptor);
        } else {

            String[] message = {ExceptionMessage.INTERCEPTOR_TEMPLATE_NOT_EXIST.getMessage(), interceptor.getId().toString(), interceptor.getType().name(), interceptor.getExecutionPoint().name()};
            String erro = StringUtils.join(", ", message);
            log.error(erro);

            return null;
        }
    }

    /**
     * Removes a {@link Interceptor} file.
     *
     * @param interceptor {@link InterceptorFileDTO}
     */
    public void removeFileInterceptor(InterceptorFileDTO interceptor) {

        File interceptorFile = new File(interceptor.getPath());

        if (interceptorFile.exists() && interceptorFile.isFile()) {
            String filter = interceptorFile.getAbsolutePath() + interceptorFile.getName();
            if (interceptorFile.delete()) {
                log.info("File - Removing File Filter: {}", interceptorFile.getAbsolutePath());
                FilterRegistry.instance().remove(filter);
                clearLoaderCache();
                log.debug("FilterRegistry - Removing File Filter {}", filter);
            } else {
                log.warn("Not possible to remove File: {} with Interceptor ID: {}", interceptorFile.getAbsolutePath(), interceptor.getId());
            }
        }
    }

    /*
     * Clears the loader cache
     */
    private void clearLoaderCache() {
        FilterLoader filterLoader = FilterLoader.getInstance();
        Field field = ReflectionUtils.findField(FilterLoader.class, "hashFiltersByType");
        ReflectionUtils.makeAccessible(field);
        Map<?, ?> cache = (Map<?, ?>) ReflectionUtils.getField(field, filterLoader);
        cache.clear();
    }

    /*
     * Creates a File that represents the Interceptor type. If its a type Log, adds the execution point
     */
    private File templateInterceptor(TypeInterceptor type, TypeExecutionPoint executionPoint) {

        File file = null;
        String filePath = "template-interceptor";
        try {
            file = ResourceUtils.getFile(filePath + File.separator + type.getHeimdallInterceptor().getFile(executionPoint));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return file;
    }

    /*
     * Defines the prefix order of a InterceptorLifeCycle.
     */
    private Integer definePrefixOrder(InterceptorLifeCycle lifeCycle) {

        Integer prefixOrder = 0;
        switch (lifeCycle) {
            case API:
                prefixOrder = 1;
                break;
            case PLAN:
                prefixOrder = 2;
                break;
            case RESOURCE:
                prefixOrder = 3;
                break;
            case OPERATION:
                prefixOrder = 4;
                break;
            default:
                break;
        }

        return prefixOrder;

    }

    /*
     * Returns a Set<String> of allowed paths of a Interceptor.
     */
    private Set<String> pathsAllowed(Interceptor interceptor) {

        Set<String> patterns = Sets.newHashSet();
        switch (interceptor.getLifeCycle()) {
            case API:
                patterns = Sets.newHashSet(interceptor.getApi().getBasePath());
                break;
            case PLAN:
                patterns = Sets.newHashSet(interceptor.getPlan().getApi().getBasePath());
                break;
            case RESOURCE:
                List<Operation> operations = operationRepository.findByResourceId(interceptor.getResource().getId());
                operations.sort(new OperationSort());
                if (Objeto.notBlank(operations)) {

                    for (Operation operation : operations) {

                        patterns.add(operation.getResource().getApi().getBasePath() + operation.getPath());
                    }
                }
                break;
            case OPERATION:
                patterns.add(interceptor.getOperation().getResource().getApi().getBasePath() + interceptor.getOperation().getPath());
                break;
            default:
                break;
        }

        return patterns;
    }

    /*
     * Returns a Set<String> of not allowed paths of a Interceptor.
     */
    private Set<String> pathsNotAllowed(Interceptor interceptor) {

        Set<String> pathsNotAllowed = Sets.newHashSet();
        if (Objeto.notBlank(interceptor) && Objeto.notBlank(interceptor.getIgnoredResources())) {

            for (Resource resource : interceptor.getIgnoredResources()) {

                if (Objeto.notBlank(resource.getOperations())) {

                    for (Operation operation : resource.getOperations()) {

                        pathsNotAllowed.add(resource.getApi().getBasePath() + operation.getPath());
                    }
                }
            }
        }

        if (Objeto.notBlank(interceptor) && Objeto.notBlank(interceptor.getIgnoredOperations())) {

            for (Operation operation : interceptor.getIgnoredOperations()) {

                pathsNotAllowed.add(operation.getResource().getApi().getBasePath() + operation.getPath());

            }
        }

        return pathsNotAllowed;
    }

    private HashMap<String, Object> buildCustom(HashMap<String, Object> parameters, Interceptor interceptor) {

        Object objectCustom = interceptor.getType().getHeimdallInterceptor().parseContent(interceptor.getContent());

        if (Objeto.notBlank(objectCustom)) {

            if (objectCustom instanceof AccessTokenClientIdDTO) {

                if (TypeInterceptor.ACCESS_TOKEN.equals(interceptor.getType())) {

                    InterceptorLifeCycle lifeCycle = interceptor.getLifeCycle();
                    List<Interceptor> interceptors = Lists.newArrayList();
                    switch (lifeCycle) {
                        case API:
                            interceptors = interceptorRepository.findByTypeAndApiId(TypeInterceptor.CLIENT_ID, interceptor.getApi().getId());
                            break;
                        case PLAN:
                            interceptors = interceptorRepository.findByTypeAndPlanId(TypeInterceptor.CLIENT_ID, interceptor.getPlan().getId());
                            break;
                        case RESOURCE:
                            interceptors = interceptorRepository.findByTypeAndResourceId(TypeInterceptor.CLIENT_ID, interceptor.getResource().getId());
                            break;
                        case OPERATION:
                            interceptors = interceptorRepository.findByTypeAndOperationId(TypeInterceptor.CLIENT_ID, interceptor.getOperation().getId());
                            break;
                        default:
                            break;
                    }

                    for (Interceptor i : interceptors) {

                        try {
                            AccessTokenClientIdDTO clientIdDTO = JsonUtils.convertJsonToObject(i.getContent(), AccessTokenClientIdDTO.class);
                            if (Objeto.notBlank(clientIdDTO) && Objeto.notBlank(clientIdDTO.getName())) {

                                parameters.put("client_id", clientIdDTO.getName());
                                break;
                            }

                        } catch (IOException e) {

                            log.error(e.getMessage(), e);
                        }
                    }
                }
            }

            parameters = interceptor.getType().getHeimdallInterceptor().buildParameters(objectCustom, parameters, interceptor);
        }

        return parameters;
    }

    private void generateFileInterceptor(Interceptor interceptor, String template, HashMap<String, Object> parameters) {

        try {
            String codeInterceptor = GenerateMustache.generateTemplate(template, parameters);
            String fileName = StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId().toString()) + ".groovy";

            String pathName;
            if (TypeInterceptor.MIDDLEWARE.equals(interceptor.getType())) {

                pathName = String.join(File.separator, zuulFilterRoot, MIDDLEWARE_API_ROOT, interceptor.getOperation().getResource().getApi().getId().toString(), fileName);
            } else {

                pathName = String.join(File.separator, zuulFilterRoot, interceptor.getExecutionPoint().getFilterType(), fileName);
            }
            File file = new File(pathName);

            Files.write(codeInterceptor, file, Charsets.UTF_8);
        } catch (IOException e) {

            log.error(e.getMessage(), e);
        }

    }

    /*
     * Creates the path to be used as key for the RateLimit repository
     */
    private String createPath(Interceptor interceptor) {

        String path =  "";

        switch (interceptor.getLifeCycle()) {
            case API: {
                path = interceptor.getApi().getBasePath();
                break;
            }
            case PLAN: {
                path = interceptor.getPlan().getApi().getBasePath();
                break;
            }

            case RESOURCE: {
                path = interceptor.getResource().getApi().getBasePath() + "-" + interceptor.getResource().getName();
                break;
            }
            case OPERATION: {
                path = interceptor.getOperation().getResource().getApi().getBasePath() + "-" +
                        interceptor.getOperation().getResource().getName() + "-" +
                        interceptor.getOperation().getPath();
                break;
            }
        }
        return path;
    }
}
