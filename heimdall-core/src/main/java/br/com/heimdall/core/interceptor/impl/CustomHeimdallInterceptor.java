package br.com.heimdall.core.interceptor.impl;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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

import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.enums.TypeExecutionPoint;
import br.com.heimdall.core.interceptor.HeimdallInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the HeimdallInterceptor to type Custom.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class CustomHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "custom.mustache";
    }

    @Override
    public String parseContent(String content) {
        return content;
    }

    @Override
    public Map<String, Object> buildParameters(Interceptor interceptor) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put("content", interceptor.getContent());

        return parameters;
    }
}
