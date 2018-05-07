
package br.com.conductor.heimdall.core.util;

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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * This class holds miscellaneous constants.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 * @author Marcelo Rodrigues
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

     public static final String PRODUCTION = "prod";
     public static final String SUCCESS = "SUCCESS";
     public static final String FAILED = "FAILED";

     public static final String INTERRUPT = "INTERRUPT";
     
     public static final String MIDDLEWARE_ROOT = "middleware";

     public static final String MIDDLEWARE_API_ROOT = "api";
     
}
