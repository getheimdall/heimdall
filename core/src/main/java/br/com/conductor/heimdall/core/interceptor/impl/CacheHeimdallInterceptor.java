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
package br.com.conductor.heimdall.core.interceptor.impl;

import br.com.conductor.heimdall.core.dto.interceptor.CacheDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.InterceptorType;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.interceptor.HeimdallInterceptor;
import br.com.conductor.heimdall.core.util.JsonUtils;
import br.com.conductor.heimdall.core.util.TemplateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the {@link HeimdallInterceptor} to provide the Caching feature.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Slf4j
public class CacheHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "cache.mustache";
    }

    @Override
    public CacheDTO parseContent(String content) {
        try {
            return JsonUtils.convertJsonToObject(content, CacheDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(InterceptorType.CACHE.name(), TemplateUtils.TEMPLATE_CACHE);
        }

        return null;
    }

    @Override
    public Map<String, Object> buildParameters(Interceptor interceptor) {

        Map<String, Object> parameters = new HashMap<>();

        CacheDTO cacheDTO = this.parseContent(interceptor.getContent());

        parameters.put("cache", cacheDTO.getCache());
        parameters.put("timeToLive", cacheDTO.getTimeToLive());
        parameters.put("headers", cacheDTO.getHeaders());
        parameters.put("queryParams", cacheDTO.getQueryParams());

        return parameters;
    }
}
