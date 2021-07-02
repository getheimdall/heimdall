
package br.com.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides methods to create and retrieve information from a <i>json</i> type String.<br/> 
 * 
 * @author Filipe Germano
 *
 */
public final class JsonUtils {

    private JsonUtils() { }

    /**
     * Converts a java Object to a <i>json</i> String representation.
     *
     * @param  object						The object to be converter
     * @return								The json String representation of the object
     * @throws JsonProcessingException
     */
     public static <T> String convertObjectToJson(T object) throws JsonProcessingException {
          
          ObjectMapper mapper = new ObjectMapper();
          return mapper.writeValueAsString(object);
     }

     /**
      * Maps a json String to a class structure.
      * 
      * @param  json						The json to be mapped
      * @param  clazz						The class structure that represents the json
      * @return								The object created from the json
      * @throws IOException
      */
     public static <T> T convertJsonToObject(String json, Class<T> clazz) throws IOException {
          
          ObjectMapper mapper = new ObjectMapper();
          return mapper.readValue(json, clazz);
     }

}
