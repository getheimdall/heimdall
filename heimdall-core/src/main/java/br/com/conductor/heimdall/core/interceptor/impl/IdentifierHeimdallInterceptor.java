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

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interceptor.HeimdallInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the HeimdallInterceptor to type Identifier.
 *
 * @author Marcelo Aguiar Rodrigues
 *
 */
public class IdentifierHeimdallInterceptor implements HeimdallInterceptor {
    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "identifier.mustache";
    }

    @Override
    public String parseContent(String content) {
        return "";
    }

    @Override
    public Map<String, Object> buildParameters(Interceptor interceptor) {
        return new HashMap<>();
    }
}
