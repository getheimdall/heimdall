
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
 * This interface represents a call made to the api.
 *
 * @author Filipe Germano
 *
 */
public interface Call {

	 /**
	  * Gets the request from the call.
	  * 
	  * @return		The request from the call
	  */
     public Request request();

     /**
	  * Gets the response from the call.
	  * 
	  * @return		The response from the call
	  */
     public Response response();
     
     /**
	  * Gets the trace from the call.
	  * 
	  * @return		The trace from the call
	  */
     public Trace trace();
     
     /**
	  * Gets the environment from the call.
	  * 
	  * @return		The environment from the call
	  */
     public Environment environment();

     /**
      * Gets the info from the call.
      * 
      * @return		The info from the call
      */
     public Info info();

}
