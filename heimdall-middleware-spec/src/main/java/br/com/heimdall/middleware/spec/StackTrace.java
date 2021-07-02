
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

/**
 * This interface provides methods to control a StackTrace.
 *
 * @author Filipe Germano
 *
 */
public interface StackTrace {

	 /**
	  * Gets the class from which the stack is tracking.
	  *  
	  * @return			The name of the class
	  */
     public String getClazz();

     /**
      * Sets the class from which the stack should tracking.
      * 
      * @param clazz	The name of the class
      */
     public void setClazz(String clazz);

     /**
      * Gets the current message from the StackTrace.
      * 
      * @return			The message
      */
     public String getMessage();

     /**
      * Sets a message to the StackTrace.
      * 
      * @param message	The message
      */
     public void setMessage(String message);

     /**
      * Gets the current stack.
      * 
      * @return			The current stack
      */
     public String getStack();

     /**
      * Sets the current stack.
      * 
      * @param stack	The new stack
      */
     public void setStack(String stack);
}
