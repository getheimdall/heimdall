/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.InterceptorType;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.GenerateMustache;
import br.com.conductor.heimdall.core.util.StringUtils;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.filters.FilterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides methods to create and remove the {@link Interceptor} files.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
@Service
@Slf4j
public class InterceptorFileService {
	
    @Value("${zuul.filter.root}")
    private String zuulFilterRoot;

    private static final String API_ID = "apiId";
    private static final String EXECUTION_POINT = "executionPoint";
    private static final String IGNORED_OPERATIONS = "ignoredOperations";
    private static final String INTERCEPTOR_ID = "interceptor-id";
    private static final String INTERCEPTOR_STATUS = "interceptorStatus";
    private static final String INTERCEPTOR_TYPE = "interceptorType";
    private static final String LIFECYCLE = "lifeCycle";
    private static final String NAME = "name";
    private static final String ORDER = "order";
    private static final String REFERENCE_ID = "referenceId";
    private static final String ZUUL_FILTER_ROOT = "zuulFilterRoot";

    /**
     * Creates a {@link Interceptor} from its Id.
     *
     * @param interceptor The {@link Interceptor}
     */
    @Transactional(readOnly = true)
    public void createFileInterceptor(Interceptor interceptor) {

        HeimdallException.checkThrow(interceptor == null, ExceptionMessage.INTERCEPTOR_NOT_EXIST);

        String template = templateInterceptor(interceptor.getType(), interceptor.getExecutionPoint());

        if (template != null) {

            final Map<String, Object> parameters = buildParametersFile(interceptor);

            generateFileInterceptor(template, parameters);
        } else {
            String[] message = {ExceptionMessage.INTERCEPTOR_TEMPLATE_NOT_EXIST.getMessage(), interceptor.getId(), interceptor.getType().name(), interceptor.getExecutionPoint().name()};
            String error = String.join(", ", message);
            log.error(error);
        }
    }

    /*
     * Constructs a parameter file from a Interceptor.
     */
    private Map<String, Object> buildParametersFile(Interceptor interceptor) {

        final long INVALID_REFERENCE_ID = -1L;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(API_ID, interceptor.getApiId());
        parameters.put(INTERCEPTOR_ID, interceptor.getId());
        parameters.put(EXECUTION_POINT, interceptor.getExecutionPoint().getFilterType());
        parameters.put(IGNORED_OPERATIONS, interceptor.getIgnoredOperations());
        parameters.put(INTERCEPTOR_STATUS, interceptor.getStatus());
        parameters.put(INTERCEPTOR_TYPE, interceptor.getType());
        parameters.put(LIFECYCLE, interceptor.getLifeCycle().name());
        parameters.put(NAME, StringUtils.concatCamelCase(interceptor.getLifeCycle().name(), interceptor.getType().name(), interceptor.getExecutionPoint().getFilterType(), interceptor.getId()));
        parameters.put(ORDER, StringUtils.generateOrder(definePrefixOrder(interceptor.getLifeCycle()), interceptor.getOrder()));
        parameters.put(REFERENCE_ID, (interceptor.getReferenceId() != null) ? interceptor.getReferenceId() : INVALID_REFERENCE_ID);
        parameters.put(ZUUL_FILTER_ROOT, zuulFilterRoot);

        parameters.putAll(interceptor.getType().getHeimdallInterceptor().buildParameters(interceptor));

        return parameters;
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
     * Recovers the String representation of the template for the interceptor
     */
    private String templateInterceptor(InterceptorType type, TypeExecutionPoint executionPoint) {

        String result = null;
        String filePath = "template-interceptor";
        try (
                InputStream inputStream = new ClassPathResource(
                        filePath +
                        File.separator +
                        type.getHeimdallInterceptor().getFile(executionPoint)).getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
        ) {
            result = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /*
     * Defines the prefix order of a InterceptorLifeCycle.
     */
    private Integer definePrefixOrder(InterceptorLifeCycle lifeCycle) {

        switch (lifeCycle) {
            case API: return 1;
            case PLAN: return 2;
            case RESOURCE: return 3;
            case OPERATION: return 4;
            default: return 0;
        }

    }

    private void generateFileInterceptor(String template, Map<String, Object> parameters) {

        try {
            String codeInterceptor = GenerateMustache.generateTemplate(template, parameters);
            String fileName = parameters.get("name") + ".groovy";

            File file = new File(
                    String.join(File.separator, zuulFilterRoot, parameters.get("executionPoint").toString(), fileName)
            );

            FileUtils.writeStringToFile(file, codeInterceptor, StandardCharsets.UTF_8);
        } catch (IOException e) {

            log.error(e.getMessage(), e);
        }

    }

}
