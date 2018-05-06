
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
 * <h1>Query</h1><br/>
 * 
 * This interface provides methods to create, read, update and delete a Query.
 *
 * @author Filipe Germano
 *
 */
public interface Query {
     
	 /**
	  * Gets all queries.
	  * 
	  * @return		A Map<String, String> with all the queries
	  */
     public Map<String, String> getAll();
     
     /**
      * Gets a specific query.
      * 
      * @param name		- The name of the query
      * @return			The query found
      */
     public String get(String name);
     
     /**
      * Sets a query.
      * 
      * @param name		- The name of the query to be set
      * @param value	- The value of the query to be set
      */
     public void set(String name, String value);
     
     /**
      * Adds a query.
      * 
      * @param name		- The name of the query to be set
      * @param value	- The value of the query to be set
      */
     public void add(String name, String value);
     
     /**
      * Removes a query.
      *  
      * @param name		- The name of the query to be set
      */
     public void remove(String name);

}
