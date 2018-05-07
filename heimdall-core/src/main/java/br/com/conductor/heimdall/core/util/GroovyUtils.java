
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

import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import groovy.lang.GroovyClassLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provides a method to compile Groovy resources.
 * 
 * @author Filipe Germano
 *
 */
@Slf4j
public abstract class GroovyUtils {

	 /**
	  * Maps a groovy object to a java Object.
	  *  
	  * @param  groovy			The groovy object to be converted
	  * @param  clazz			The java class representation of the groovy
	  * @return					The java object created, returns null if operation failed
	  */
     @SuppressWarnings({ "resource", "unchecked" })
     public static <T> T compile(String groovy, Class<T> clazz) {

          try {
               
               GroovyClassLoader classLoader = new GroovyClassLoader();
               clazz = classLoader.parseClass(groovy);
               T newInstance = clazz.newInstance();
               
               return newInstance;
          } catch (InstantiationException | IllegalAccessException | MultipleCompilationErrorsException | SecurityException | IllegalArgumentException e) {
               
               log.error(e.getMessage(), e);
               return null;
          }
     }
}
