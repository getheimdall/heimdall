
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
 * This interface provides methods to control a Trace.
 *
 * @author Filipe Germano
 *
 */
public interface Trace {
     
	 /**
	  * Adds a Trace to the stack.
	  * 
	  * @param clazz		Class that generated the trace
	  * @param message		Message generated
	  * @param stack		Stack that will receive the trace
	  */
     public void addStackTrace(String clazz, String message, String stack);
     
     /**
      * Gets the {@link StackTrace}.
      * 
      * @return				The current StackTrace
      */
     public StackTrace getStackTrace();

     /**
      * Adds a Trace.
      * 
      * @param trace		The Trace to be added
      */
     public void addTrace(String trace);

     /**
      * Adds a Trace for a object.
      * 
      * @param trace		The Trace to be added
      * @param object		The object that should be traced
      */
     public void addTrace(String trace, Object object);
     
}
