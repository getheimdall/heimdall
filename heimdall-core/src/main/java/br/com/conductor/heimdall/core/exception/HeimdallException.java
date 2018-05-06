
package br.com.conductor.heimdall.core.exception;

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

import lombok.Getter;

/**
 * <h1>HeimdallException</h1><br/>
 * 
 * This class represents all the Heimdall personalized exceptions.
 * 
 * @author Filipe Germano
 * @see RuntimeException
 * 
 */
public class HeimdallException extends RuntimeException {

     private static final long serialVersionUID = 4942916592031161727L;
     
     @Getter
     private ExceptionMessage msgEnum;

     public HeimdallException(ExceptionMessage exceptionMessage) {

          super(exceptionMessage.getMessage());
          this.msgEnum = exceptionMessage;

     }

     public static void checkThrow(boolean expression, ExceptionMessage exceptionMessage) throws HeimdallException {

          if (expression) {
               exceptionMessage.raise();
          }
     }

     public static void checkThrow(boolean expression, ExceptionMessage exceptionMessage, String... dynamicText) throws HeimdallException {

          if (expression) {
               exceptionMessage.raise(dynamicText);
          }
     }

}
