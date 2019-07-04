/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.core.exception;

import lombok.Getter;

/**
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

     /**
      * Creates a new Heimdall Exception.
      * 
      * @param exceptionMessage  {@link ExceptionMessage}
      */
     public HeimdallException(ExceptionMessage exceptionMessage) {

          super(exceptionMessage.getMessage());
          this.msgEnum = exceptionMessage;

     }

     /**
      * Throws a new exception with a {@link ExceptionMessage} if the expression is true.
      * 
      * @param expression	if true, throws expection
      * @param exceptionMessage	{@link ExceptionMessage}
      * @throws HeimdallException {@link HeimdallException}
      */
     public static void checkThrow(boolean expression, ExceptionMessage exceptionMessage) throws HeimdallException {

          if (expression) {
               exceptionMessage.raise();
          }
     }

     /**
      * Throws a new custom exception with a {@link ExceptionMessage} with the dynamicText injected.
      * 
      * @param expression	if true, throws expection
      * @param exceptionMessage	{@link ExceptionMessage}
      * @param dynamicText	text to be injected to the message
      * @throws HeimdallException {@link HeimdallException}
      */
     public static void checkThrow(boolean expression, ExceptionMessage exceptionMessage, String... dynamicText) throws HeimdallException {

          if (expression) {
               exceptionMessage.raise(dynamicText);
          }
     }

}
