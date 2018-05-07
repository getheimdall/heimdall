
package br.com.conductor.heimdall.gateway.zuul.route;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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

import org.springframework.cloud.netflix.zuul.filters.Route;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class that represents a route inside Heimdall.
 *
 * @author Marcos Filho
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeimdallRoute {

     private String patternMatched;

     private Route route;

     private boolean methodNotAllowed;
     
     /**
      * Sets a method not allowed.
      * 
      * @return The {@link HeimdallRoute} updated
      */
     public HeimdallRoute methodNotAllowed() {

          this.methodNotAllowed = true;
          return this;
     }
}
