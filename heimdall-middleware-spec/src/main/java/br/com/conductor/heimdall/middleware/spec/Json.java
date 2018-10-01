
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

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.conductor.heimdall.middleware.exception.BeanValidationException;

import java.util.Map;

/**
 * This interface provides methods to parse Json Strings to a java object and reverse.
 *
 * @author Filipe Germano
 *
 */
public interface Json {
     
	 /**
	  * Converts a Map of objects to a json string.
	  * 
	  * @param body		The Map of objects to be converted
	  * @return			The json string created
	  */
     public String parse(Map<String, Object> body);

     /**
      * Parses a string to a json string.
      * 
      * @param string	The string to be parsed
      * @return			The parsed json string
      */
     public String parse(String string);

     /**
      * Parses a Object to a json string.
      * 
      * @param object	The object to be parsed
      * @return			The parsed json string
      */
     public <T> String parse(T object);

     /**
      * Parses a json string to a generic type
      *
      * @param json
      * @param classType
      * @return
      */
     public <T> T parse(String json, Class<?> classType) throws BeanValidationException;

     /**
      * Parses a json string to a parametrized class through a list of parameters classes.
      *
      * @param json
      * @param parametrized
      * @param parameterClasses
      * @param <T>
      * @return
      */
     public <T> T parse(String json, Class<?> parametrized, Class<?>... parameterClasses) throws BeanValidationException;

     /**
      * Parses a json string to a generic type through TypeReference object.
      *
      * @param json
      * @param type
      * @return
      */
     public <T> T parse(String json, TypeReference<T> type) throws BeanValidationException;
     
     /**
      * Parses a object to a Map<String, Object>.
      * 
      * @param object	The object to be parsed
      * @return			The Map representation of the object
      */
     public <T> Map<String, Object> parseToMap(T object);
     
     /**
      * Validate this is Json valid.
      * 
      * @param string    - The string to be validate
      * @return               boolean
      */
     public boolean isJson(String string);
     
}
