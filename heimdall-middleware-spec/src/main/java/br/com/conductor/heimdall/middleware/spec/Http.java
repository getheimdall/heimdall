
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

import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This interface provides methods to handle a {@link Http} and a {@link ApiResponse}.
 *
 * @author Filipe Germano
 *
 */
public interface Http {

	 /**
	  * Sets a header to a Http.
	  * 
	  * @param name		The name of the header
	  * @param value	The value of the header
	  * @return			The Http with the new header added
	  */
     public Http header(String name, String value);

     /**
      * Sets a header to a Http.
	  * 
      * @param params	The Map with name and value of the headers
      * @return			The Http with the new headers added 
      */
     public Http header(Map<String, String> params);

     /**
      * Sets a url to a Http.
      * 
      * @param url		The url
      * @return			The Http with the url added
      */
     public Http url(String url);

     /**
      * Sets query parameters to the Http.
      * 
      * @param name		The name of the query
      * @param value	The value of the query
      * @return			The Http with the query added
      */
     public Http queryParam(String name, String value);

     /**
      * Sets a body to the Http.
      * 
      * @param params	The Map representation of the body
      * @return			The updated Http
      */
     public Http body(Map<String, Object> params);

     /**
      * Sets a body to the Http.
      * 
      * @param params   The String representation of the body
      * @return               The updated Http
      */
     public Http body(String params);
     
     /**
      * Sends a GET request to the Api and receives a {@link ApiResponse}.
      * 
      * @return			A ApiResponse object
      */
     public ApiResponse sendGet();

     /**
      Sends a POST request to the Api and receives a {@link ApiResponse}.
      * 
      * @return			A ApiResponse object
      */
     public ApiResponse sendPost();

     /**
      Sends a PUT request to the Api and receives a {@link ApiResponse}.
      * 
      * @return			A ApiResponse object
      */
     public ApiResponse sendPut();

     /**
      Sends a DELETE request to the Api and receives a {@link ApiResponse}.
      * 
      * @return			A ApiResponse object
      */
     public ApiResponse sendDelete();

    /**
     * Sends a PATCH request to the Api and receives a {@link ApiResponse}.
     *
     * @return			A ApiResponse object
     */
    public ApiResponse sendPatch();

    /**
     * Set RestTemplate custom object
     * @return               A RestTemplate object
     */
     public RestTemplate clientProvider(RestTemplate restTemplate);
}
