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
package br.com.conductor.heimdall.core.util;

import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import com.github.thiagonego.alfred.object.Objeto;

/**
 * This class provides a methods to format {@link String}s to be used by Heimdall. 
 * 
 * @author Filipe Germano
 *
 */
public abstract class StringUtils {

	/**
	 * Creates a String that represents a order. It adds leading zeros ("0").
	 * 
	 * @param  prefix					The number to be prefixed
	 * @param  order					The order
	 * @return							The prefixed order created
	 */
     public static String generateOrder(Integer prefix, Integer order) {

          String value = String.format("%s%s", prefix, org.apache.commons.lang.StringUtils.leftPad(order.toString(), 2, "0"));
          HeimdallException.checkThrow(value.length() > 3, ExceptionMessage.INTERCEPTOR_LIMIT_REACHED);
          
          return value;
     }
     
     /**
      * Converts multiple parameters of type String from UPPER_SPLIT case to camelCase and concatenate them.
      * 
      * @param  strings				Multiple String parameters
      * @return						The concatenated String
      */
     public static String concatCamelCase(String... strings) {
          
          String value = "";
          for (String string : strings) {
               
               String[] splits = string.split("_");
               for (String split : splits) {
                    
                    if (Objeto.notBlank(split)) {
                         
                         value += split.substring(0, 1).toUpperCase() + split.substring(1).toLowerCase();
                    }
               }
          }
          
          return value;          
     }

     /**
      * Concatenates multiple strings.
      * 
      * @param  strings				Multiple String parameters 
      * @return						The concatenated String
      */
     public static String join(String... strings) {
          
          return org.apache.commons.lang.StringUtils.join(strings);
          
     }

     /**
      * Concatenates multiple strings with a specific separator.
      * 
      * @param 	separator			The separator to be used.
      * @param  strings				Multiple String parameters 
      * @return						The concatenated String
      */
     public static String join(String separator, String... strings) {
          
          return org.apache.commons.lang.StringUtils.join(strings, separator);
          
     }

}
