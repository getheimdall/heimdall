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
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.gateway.trace.RequestResponseParser;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Log Mask Service provides a method to mask the information from the {@link br.com.conductor.heimdall.gateway.trace.Trace}.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class LogMaskerService {

    /**
     * Given the filter type it removes the information passed from the {@link br.com.conductor.heimdall.gateway.trace.Trace}.
     *
     * @param filterType     {@link org.springframework.cloud.netflix.zuul.filters.support.FilterConstants}
     * @param deleteBody     should delete the request body
     * @param deleteUri      should delete the request uri
     * @param headers        should delete the request headers
     * @param ignoredHeaders headers that should be deleted, if empty all will be deleted
     */
    public void execute(String filterType,
                        Boolean deleteBody,
                        Boolean deleteUri,
                        Boolean headers,
                        List<String> ignoredHeaders) {

        RequestResponseParser requestResponseParser = null;

        switch (filterType) {
            case PRE_TYPE:
                requestResponseParser = TraceContextHolder.getInstance().getActualTrace().getRequest();
                break;

            case POST_TYPE:
                requestResponseParser = TraceContextHolder.getInstance().getActualTrace().getResponse();
                break;

            default:
                break;
        }

        if (requestResponseParser != null) {

            // Should delete body
            if (deleteBody) requestResponseParser.setBody(null);

            // Should delete URI
            if (deleteUri) requestResponseParser.setUri(null);

            // Should delete headers
            if (headers) {
                if (ignoredHeaders.isEmpty()) {
                    requestResponseParser.setHeaders(null);
                } else {
                    final Map<String, String> requestResponseHeaders = requestResponseParser.getHeaders();
                    ignoredHeaders.forEach(requestResponseHeaders::remove);
                }
            }
        }
    }
}
