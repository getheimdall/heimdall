
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

/**
 * <h1>UnauthorizedException</h1><br/>
 * 
 * This class represents the exceptions related to resource requests that were rejected by the bussiness logic.
 *
 * @author Filipe Germano
 *
 */
public class UnauthorizedException extends MiddlewareException {

     private static final long serialVersionUID = -190925457848682557L;

     public UnauthorizedException(ExceptionMessage exeptionMessage) {
          super(exeptionMessage);
     }
     
}
