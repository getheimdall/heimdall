
package br.com.conductor.heimdall.core.enums;

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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import br.com.conductor.heimdall.core.entity.Interceptor;

/**
 * <h1>Should</h1><br/>
 * 
 * Provides method to validate the path of a {@link Interceptor}.
 *
 * @author Filipe Germano
 *
 */
public interface Should {
     
	/**
	 * Validates if a inbound {@link Interceptor} URL is valid.
	 * 
	 * @param path				- The Set that represents the path
	 * @param pathsAllowed		- The Set of allowed paths
	 * @param inboundURL		- The inbound URL
	 * @param method			- The HTTP method
	 * @param req				- The {@link HttpServletRequest}
	 * @return					True if the inbound URL is valid, false otherwise
	 */
     public boolean filter(Set<String> path, Set<String> pathsAllowed, String inboundURL, String method, HttpServletRequest req);

}
