
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
 * This interface provides methods to control a request.
 *
 * @author Filipe Germano
 *
 */
public interface Request {
     
	 /**
	  * Gets the header of the request.
	  * 
	  * @return		The header of the request
	  */
     public Header header();          

     /**
	  * Gets the query of the request.
	  * 
	  * @return		The query of the request
      */
     public Query query();

     /**
	  * Gets the body of the request.
	  * 
	  * @return		The body of the request
      */
     public String getBody();
     
     /**
      * Sets the body of a request.
      * 
      * @param body		The body of the request to be set
      */
     public void setBody(String body);
     
     /**
      * Sets a url to the request.
      * 
      * @param routeUrl	The url to the request
      */
     public void setUrl(String routeUrl);
     
     /**
      * Gets the path of a parameter by its name.
      * 
      * @param name 	The name of the path
      * @return			The path to the parameter
      */
     public String pathParam(String name);
     
     public String getAppName();
     
     public void setSendResponse(boolean value);     

}
