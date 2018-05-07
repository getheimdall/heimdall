
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

/**
 * This interface provides methods to control a response.
 *
 * @author Filipe Germano
 *
 */
public interface Response {

	 /**
	  * Gets the header of a response.
	  * 
	  * @return			The header of a response
	  */
     public Header header();
     
     /**
      * Gets the status code of a response.
      * 
      * @return			The status code of a response.
      */
     public Integer getStatus();
     
     /**
      * Sets the status code of a response.
      * @param status	The the status code of a response
      */
     public void setStatus(Integer status);
     
     /**
      * Gets the body of a response.
      * 
      * @return			The body of a response.
      */
     public String getBody();
     
     /**
      * Sets the body of a response.
      * 
      * @param body		The String representation of body of a response
      */
     public void setBody(String body);
     
     /**
      * Sets the body of a response.
      * 
      * @param body		The byte array representation of body of a response
      */
     public void setBody(byte[] body);
}
