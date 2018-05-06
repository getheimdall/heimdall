
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

/**
 * <h1>NotFoundException</h1><br/>
 * 
 * This calss represents the exceptions where the request was not found.
 * 
 * @author Filipe Germano
 * @see HeimdallException
 *
 */
public class NotFoundException extends HeimdallException{

     private static final long serialVersionUID = -4118951609307998421L;

     public NotFoundException(ExceptionMessage exeptionMessage){
          super(exeptionMessage);
     }
}
