
package br.com.conductor.heimdall.gateway.configuration;

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

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.web.ZuulController;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Extends {@link ZuulHandlerMapping} to register the routes at the application start.
 *
 * @author Marcos Filho
 * @author Marcelo Aguiar Rodrigues
 *
 */
public class HeimdallHandlerMapping extends ZuulHandlerMapping {

     private final ZuulController zuul;

     private volatile boolean dirty = true;

     public HeimdallHandlerMapping(RouteLocator routeLocator, ZuulController zuul) {

          super(routeLocator, zuul);
          this.zuul = zuul;
     }

     @Override
     protected Object lookupHandler(String urlPath, HttpServletRequest request) throws Exception {

          if (this.dirty) {
               synchronized (this) {
                    if (this.dirty) {
                         registerHandler("/**", this.zuul);
                         setDirty(false);
                    }
               }
          }
          return super.lookupHandler(urlPath, request);
     }

     public void setDirty(boolean dirty) {

          this.dirty = dirty;
          super.setDirty(false);
     }

     public void initHandlers() {
         registerHandler("/**", this.zuul);
     }

}
