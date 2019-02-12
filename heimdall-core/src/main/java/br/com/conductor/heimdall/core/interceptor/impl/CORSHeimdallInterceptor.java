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
package br.com.conductor.heimdall.core.interceptor.impl;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.interceptor.HeimdallInterceptor;
import br.com.conductor.heimdall.core.util.JsonUtils;
import br.com.conductor.heimdall.core.util.TemplateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the HeimdallInterceptor to type CORS.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Slf4j
public class CORSHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "cors-pre.mustache";
    }

    @Override
    public Object parseContent(String content) {
        try {
            Map<String, String> map = (Map<String, String>) JsonUtils.convertJsonToObject(content, Map.class);
            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(TypeInterceptor.CORS.name(), TemplateUtils.TEMPLATE_CORS);
        }

        return null;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters, Interceptor interceptor) {

        Map<String, String> cors = (Map<String, String>) objectCustom;
        parameters.put("cors", cors.entrySet());

        return parameters;
    }
}
