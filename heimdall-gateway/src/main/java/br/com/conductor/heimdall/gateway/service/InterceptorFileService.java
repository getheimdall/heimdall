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

import static br.com.conductor.heimdall.core.util.Constants.MIDDLEWARE_API_ROOT;
import static br.com.conductor.heimdall.core.util.Constants.MIDDLEWARE_ROOT;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.filters.FilterRegistry;

import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.core.dto.interceptor.AccessTokenClientIdDTO;
import br.com.conductor.heimdall.core.dto.interceptor.MockDTO;
import br.com.conductor.heimdall.core.dto.interceptor.OAuthDTO;
import br.com.conductor.heimdall.core.dto.interceptor.RateLimitDTO;
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
import br.com.conductor.heimdall.core.util.GenerateMustache;
import br.com.conductor.heimdall.core.util.JsonUtils;
import br.com.conductor.heimdall.core.util.OperationSort;
import br.com.conductor.heimdall.core.util.ResourceUtils;
import br.com.conductor.heimdall.core.util.StringUtils;
import br.com.conductor.heimdall.core.util.TemplateUtils;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.extern.slf4j.Slf4j;

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
     * @throws BadRequestException
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

        if (Objeto.notBlank(file)) {

            HashMap<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("order", StringUtils.generateOrder(definePrefixOrder(interceptor.getLifeCycle()), interceptor.getOrder()));
            parameters.put("executionPoint", interceptor.getExecutionPoint().getFilterType());
            parameters.put("pathsAllowed", pathsAllowed(interceptor));
            parameters.put("pathsNotAllowed", pathsNotAllowed(interceptor));
            parameters.put("lifeCycle", interceptor.getLifeCycle().name());
            parameters.put("name", StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId().toString()));
            if (Objeto.notBlank(interceptor.getOperation())) {

                parameters.put("method", interceptor.getOperation().getMethod().name());
            }

            if (Objeto.notBlank(interceptor.getEnvironment())) {

                parameters.put("inboundURL", interceptor.getEnvironment().getInboundURL());
            } else {

                parameters.put("inboundURL", null);
            }

            return parameters = buildCustom(parameters, interceptor);
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
        try {
            String filePath = "template-interceptor";
            switch (type) {
                case ACCESS_TOKEN:
                    file = ResourceUtils.getFile(filePath + File.separator + "access_token.mustache");
                    break;
                case CLIENT_ID:
                    file = ResourceUtils.getFile(filePath + File.separator + "client_id.mustache");
                    break;
                case MOCK:
                    file = ResourceUtils.getFile(filePath + File.separator + "mock.mustache");
                    break;
                case RATTING:
                    file = ResourceUtils.getFile(filePath + File.separator + "ratting.mustache");
                    break;
                case CUSTOM:
                    file = ResourceUtils.getFile(filePath + File.separator + "custom.mustache");
                    break;
                case LOG:
                    switch (executionPoint) {
                        case FIRST:
                            file = ResourceUtils.getFile(filePath + File.separator + "request_log.mustache");
                            break;
                        case SECOND:
                            file = ResourceUtils.getFile(filePath + File.separator + "response_log.mustache");
                            break;
                        default:
                            break;
                    }
                    break;
                case MIDDLEWARE:
                    file = ResourceUtils.getFile(filePath + File.separator + "middleware.mustache");
                    break;
                case OAUTH:
                    file = ResourceUtils.getFile(filePath + File.separator + "oauth.mustache");
                    break;
                default:
                    break;
            }

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
            case PLAN:
                prefixOrder = 1;
                break;
            case RESOURCE:
                prefixOrder = 2;
                break;
            case OPERATION:
                prefixOrder = 3;
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

        Object objectCustom = validateTemplate(interceptor.getType(), interceptor.getContent());
        if (Objeto.notBlank(objectCustom)) {

            if (objectCustom instanceof AccessTokenClientIdDTO) {

                AccessTokenClientIdDTO accessTokenClientIdDTO = (AccessTokenClientIdDTO) objectCustom;
                parameters.put("name", accessTokenClientIdDTO.getName());
                parameters.put("location", accessTokenClientIdDTO.getLocation());
                parameters.put("apiId", interceptor.getApi().getId());

                if (TypeInterceptor.ACCESS_TOKEN.equals(interceptor.getType())) {

                    InterceptorLifeCycle lifeCycle = interceptor.getLifeCycle();
                    List<Interceptor> interceptors = Lists.newArrayList();
                    switch (lifeCycle) {
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

                    for (Interceptor interc : interceptors) {

                        try {
                            AccessTokenClientIdDTO clientIdDTO = JsonUtils.convertJsonToObject(interc.getContent(), AccessTokenClientIdDTO.class);
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

            if (objectCustom instanceof MockDTO) {

                MockDTO mockDTO = (MockDTO) objectCustom;
                parameters.put("status", mockDTO.getStatus());
                parameters.put("body", mockDTO.getBody());

            }

            if (objectCustom instanceof RateLimitDTO) {

                RateLimitDTO rateLimitDTO = (RateLimitDTO) objectCustom;
                parameters.put("calls", rateLimitDTO.getCalls());
                parameters.put("interval", rateLimitDTO.getInterval().name());
            }

            if (objectCustom instanceof OAuthDTO) {

                OAuthDTO oAuthDTO = (OAuthDTO) objectCustom;

                parameters.put("providerId", Objeto.isBlank(oAuthDTO.getProviderId()) ? 0L : oAuthDTO.getProviderId());
                parameters.put("timeAccessToken", Objeto.isBlank(oAuthDTO.getTimeAccessToken()) ? 10 : oAuthDTO.getTimeAccessToken());
                parameters.put("timeRefreshToken", Objeto.isBlank(oAuthDTO.getTimeRefreshToken()) ? 1800 : oAuthDTO.getTimeRefreshToken());
                parameters.put("typeOAuth", oAuthDTO.getTypeOAuth());
            }

            if (objectCustom instanceof String) {

                parameters.put("content", objectCustom);
            }
        }

        if (TypeInterceptor.MIDDLEWARE.equals(interceptor.getType())) {

            String api = interceptor.getOperation().getResource().getApi().getId().toString();
            String pathReferences = String.join("/", zuulFilterRoot, MIDDLEWARE_API_ROOT, api, MIDDLEWARE_ROOT);
            parameters.put("pathMiddleware", pathReferences);
        }

        return parameters;
    }

    private Object validateTemplate(TypeInterceptor type, String content) {

        Object response = null;
        switch (type) {
            case ACCESS_TOKEN:
                try {

                    AccessTokenClientIdDTO accessTokenDTO = JsonUtils.convertJsonToObject(content, AccessTokenClientIdDTO.class);
                    response = accessTokenDTO;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(type.name(), TemplateUtils.TEMPLATE_ACCESS_TOKEN);
                }
                break;
            case CLIENT_ID:
                try {

                    AccessTokenClientIdDTO clientIdDTO = JsonUtils.convertJsonToObject(content, AccessTokenClientIdDTO.class);
                    response = clientIdDTO;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(type.name(), TemplateUtils.TEMPLATE_ACCESS_TOKEN);
                }
                break;
            case MOCK:
                try {

                    MockDTO mockDTO = JsonUtils.convertJsonToObject(content, MockDTO.class);
                    response = mockDTO;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(type.name(), TemplateUtils.TEMPLATE_MOCK);
                }
                break;
            case RATTING:
                try {

                    RateLimitDTO rateLimitDTO = JsonUtils.convertJsonToObject(content, RateLimitDTO.class);
                    response = rateLimitDTO;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(type.name(), TemplateUtils.TEMPLATE_RATTING);
                }
                break;
            case CUSTOM:
                try {

                    response = content;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                }
                break;
            case MIDDLEWARE:
                try {

                    response = content;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                }
                break;
            case OAUTH:
                try {

                    OAuthDTO oAuthDTO = JsonUtils.convertJsonToObject(content, OAuthDTO.class);
                    response = oAuthDTO;
                } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(type.name(), TemplateUtils.TEMPLATE_OAUTH);
                }
            default:
                break;
        }

        return response;
    }

    private void generateFileInterceptor(Interceptor interceptor, String template, HashMap<String, Object> parameters) {

        try {
            String codeInterceptor = GenerateMustache.generateTemplate(template, parameters);
            String fileName = StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId().toString()) + ".groovy";

            String pathName = null;
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

}
