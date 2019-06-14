
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
 * Provides methods to parse a xml to a JSON string with or without a provided java class structure.
 * 
 * @author Marcos Filho
 *
 */
public interface Xml {

	 /**
	  * Parses a xml to a JSON string.
	  * 
	  * @param object	XML
	  * @return			JSON string
	  */
     public <T> String parse(T object);

     /**
      * Parses a xml to a Class structure.
      * 
      * @param xml			XML
      * @param classType	Java class structure
      * @return				Java object
      */
     public <T> T parse(String xml, Class<?> classType);
}
