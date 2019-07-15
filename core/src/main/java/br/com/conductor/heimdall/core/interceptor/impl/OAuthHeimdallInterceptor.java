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

import br.com.conductor.heimdall.core.dto.interceptor.OAuthDTO;
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
 * Implementation of the HeimdallInterceptor to type OAuth.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Slf4j
public class OAuthHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "oauth.mustache";
    }

    @Override
    public OAuthDTO parseContent(String content) {
        try {
            return JsonUtils.convertJsonToObject(content, OAuthDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(InterceptorType.OAUTH.name(), TemplateUtils.TEMPLATE_OAUTH);
        }

        return null;
    }

    @Override
    public Map<String, Object> buildParameters(Interceptor interceptor) {

        Map<String, Object> parameters = new HashMap<>();
        OAuthDTO oAuthDTO = this.parseContent(interceptor.getContent());

        parameters.put("providerId", oAuthDTO.getProviderId() == null ? 0L : oAuthDTO.getProviderId());
        parameters.put("timeAccessToken", oAuthDTO.getTimeAccessToken() == null ? 0 : oAuthDTO.getTimeAccessToken());
        parameters.put("timeRefreshToken", oAuthDTO.getTimeRefreshToken() == null ? 1800 : oAuthDTO.getTimeRefreshToken());
        parameters.put("typeOAuth", oAuthDTO.getTypeOAuth());
        parameters.put("privateKey", oAuthDTO.getPrivateKey());

        return parameters;
    }
}
