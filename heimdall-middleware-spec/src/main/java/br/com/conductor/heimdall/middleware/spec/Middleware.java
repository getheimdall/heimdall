
package br.com.conductor.heimdall.middleware.spec;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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

/**
 * This is the main Middleware interface. It provides the method used to execute the Middleware.
 *
 * @author Filipe Germano
 *
 */
public interface Middleware {
     
	 /**
	  * Executes the Middleware using the {@link Helper} class defined.
	  * @param helper	The Helper class
	  */
     public void run(Helper helper);

}
