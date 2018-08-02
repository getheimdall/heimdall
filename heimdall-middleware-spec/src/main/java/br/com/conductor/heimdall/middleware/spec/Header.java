
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
 * This interface provides methods to create, read, update and delete a header.
 *
 * @author Filipe Germano
 *
 */
public interface Header {
     
	 /**
	  * Gets all headers.
	  * 
	  * @return			A Map containing all headers
	  */
     public Map<String, String> getAll();
     
     /**
      * Gets a header from name.
      * 
      * @param name		The name of the header
      * @return			The header found
      */
     public String get(String name);
     
     /**
      * Sets a header.
      * 
      * @param name		The name of the header
      * @param value	The value of the header
      */
     public void set(String name, String value);
     
     /**
      * Adds a header.
      * 
      * @param name		The name of the header
      * @param value	The value of the header
      */
     public void add(String name, String value);

    /**
     * Adds a Map of headers
     *
     * @param values    The headers to be added
     */
    public void addAll(Map<String, String> values);
     
     /**
      * Removes a header.
      * 
      * @param name		The name of the header
      */
     public void remove(String name);
     
     /**
      * Gets the HTTP method of the header
      * @return			The HTTP method
      */
     public String getMethod();

}
