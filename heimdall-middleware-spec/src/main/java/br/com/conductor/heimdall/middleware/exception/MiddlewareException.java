
package br.com.conductor.heimdall.middleware.exception;

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

import lombok.Getter;

/**
 * <h1>MiddlewareException</h1><br/>
 * 
 * Class thar represets the personalized exception of a Heimdall Middleware.
 *
 * @author Filipe Germano
 *
 */
public class MiddlewareException extends RuntimeException {

     private static final long serialVersionUID = -3474588879728085772L;

     @Getter
     private ExceptionMessage msgEnum;

     /**
      * Constructor of a MiddlewareException.
      * 
      * @param exceptionMessage		- The {@link ExceptionMessage} that should be used to create the exception
      */
     public MiddlewareException(ExceptionMessage exceptionMessage) {

          super(exceptionMessage.getMessage());
          this.msgEnum = exceptionMessage;

     }

     /**
      * Static method that check if a new MiddlewareException should be thrown.
      * 
      * @param expression				- The expression that is checked
      * @param exceptionMessage			- The {@link ExceptionMessage} that should be thrown
      * @throws MiddlewareException		If the expression is true, throw a MiddlewareExcpetion
      */
     public static void checkThrow(boolean expression, ExceptionMessage exceptionMessage) throws MiddlewareException {

          if (expression) {
               exceptionMessage.raise();
          }
     }

     /**
      * Static method that check if a new MiddlewareException should be thrown with a custom message.
      * 
      * @param expression				- The expression that is checked
      * @param exceptionMessage			- The {@link ExceptionMessage} that should be thrown
      * @param dynamicText				- The custom text that will be placed in the exception message
      * @throws MiddlewareException		If the expression is true, throw a MiddlewareExcpetion
      */
     public static void checkThrow(boolean expression, ExceptionMessage exceptionMessage, String... dynamicText) throws MiddlewareException {

          if (expression) {
               exceptionMessage.raise(dynamicText);
          }
     }

}
