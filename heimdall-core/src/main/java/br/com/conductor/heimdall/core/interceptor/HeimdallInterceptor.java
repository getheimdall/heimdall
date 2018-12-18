
package br.com.conductor.heimdall.core.interceptor;

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

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;

import java.util.HashMap;

/**
 * Provides methods that are used for all built-in Heimdall Interceptors.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
public interface HeimdallInterceptor {

    /**
     * Defines the template file for the interceptor.
     *
     * @param typeExecutionPoint {@link TypeExecutionPoint}
     * @return  The name of the template file
     */
    String getFile(TypeExecutionPoint typeExecutionPoint);

    /**
     * Method to parse the content given by the user to be used in the interceptor.
     *
     * @param content User provided content
     * @return  Parsed object
     */
    Object parseContent(String content);

    /**
     * Any parameters that should be used by the interceptor can be created here.
     *
     * @param objectCustom Custom parameter Object
     * @param parameters Map of parameters
     * @param interceptor The {@link Interceptor}
     * @return
     */
    HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters, Interceptor interceptor);
}
