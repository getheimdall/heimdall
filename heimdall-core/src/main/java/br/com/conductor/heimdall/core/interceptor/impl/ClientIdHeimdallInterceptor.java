package br.com.conductor.heimdall.core.interceptor.impl;

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

import br.com.conductor.heimdall.core.dto.interceptor.AccessTokenClientIdDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.interceptor.HeimdallInterceptor;
import br.com.conductor.heimdall.core.util.JsonUtils;
import br.com.conductor.heimdall.core.util.TemplateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * Implementation of the HeimdallInterceptor to type ClientId.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Slf4j
public class ClientIdHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "client_id.mustache";
    }

    @Override
    public Object parseContent(String content) {
        try {
            return JsonUtils.convertJsonToObject(content, AccessTokenClientIdDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(TypeInterceptor.ACCESS_TOKEN.name(), TemplateUtils.TEMPLATE_ACCESS_TOKEN);
        }

        return null;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters, Interceptor interceptor) {

        AccessTokenClientIdDTO accessTokenClientIdDTO = (AccessTokenClientIdDTO) objectCustom;
        parameters.put("location", accessTokenClientIdDTO.getLocation());

        return parameters;
    }
}
