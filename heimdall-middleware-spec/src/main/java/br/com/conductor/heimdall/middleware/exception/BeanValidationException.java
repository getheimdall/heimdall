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
 * This class represents errors related to beans validations
 * <br/>
 * You can use javax annotations to validate your bean. 
 * 
 * @author <a href="https://github.com/jscamara">Jonathan Camara</a>
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=380">JSR 380<a/>
 *
 */
@Getter
public class BeanValidationException extends RuntimeException {

     private static final long serialVersionUID = -3415601794675335171L;
     
     private String violations;
     
     public BeanValidationException(String message, String violations) {
          super(message);
          this.violations = violations;
     }
}