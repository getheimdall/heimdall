
package br.com.conductor.heimdall.middleware.spec;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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

import java.util.Map;

/**
 * This interface provides methods for accessing a Environment
 *
 * @author Filipe Germano
 *
 */
public interface Environment {

	 /**
	  * Gets a Map of the variables of a Environment.
	  * 
	  * @return		The map of variables
	  */
     public Map<String, String> getVariables();

     /**
      * Gets a specific variable from a environment.
      * 
      * @param key	The key of the variable
      * @return		The variable found
      */
     public String getVariable(String key);
}
