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

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.conductor.heimdall.core.util.ConstantsInterceptors.IDENTIFIER_ID;

/**
 * Identifier interceptor adds a unique ID to the request headers
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class IdentifierInterceptorService {

    /**
     * Adds a unique ID to the request headers
     */
    public void execute() {
        String uid = UUID.randomUUID().toString();

        RequestContext context = RequestContext.getCurrentContext();
        context.addZuulRequestHeader(IDENTIFIER_ID, uid);
    }
}
