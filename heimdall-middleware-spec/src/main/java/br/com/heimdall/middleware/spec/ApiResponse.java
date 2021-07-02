
package br.com.heimdall.middleware.spec;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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

import java.io.Serializable;
import java.util.Map;

/**
 * This interface represents a response to the Api.
 *
 * @author Filipe Germano
 *
 */
public interface ApiResponse extends Serializable {
     
	 /**
	  * Gets the body of a request.
	  * 
	  * @return			The body of the request
	  */
     public String getBody();

     /**
      * Sets the body of a request.
      * 
      * @param body		The body of the request
      */
     public void setBody(String body);

     /**
      * Gets the headers of a request.
      * 
      * @return			The headers of a request
      */
     public Map<String, String> getHeaders();

     /**
      * Sets the headers of a request.
      * 
      * @param headers	The headers of a request
      */
     public void setHeaders(Map<String, String> headers);

     /**
      * Gets the status of a request.
      * 
      * @return			The status of a request
      */
     public Integer getStatus();

     /**
      * Sets the status of a request.
      * 
      * @param status	The status of a request
      */
     public void setStatus(Integer status);

}
