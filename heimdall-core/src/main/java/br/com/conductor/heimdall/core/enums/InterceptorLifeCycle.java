
package br.com.conductor.heimdall.core.enums;

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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.twsoftware.alfred.object.Objeto;

/**
 * This enum lists the stages of a {@link Interceptor} life cycle.<br/>
 * It provides a validation for each of the stages.
 * 
 * @author Filipe Germano
 * @see Should
 *
 */
public enum InterceptorLifeCycle implements Should {

     PLAN {

          @Override
          public boolean filter(Set<String> pathsAllowed, Set<String> pathsNotAllowed, String inboundURL, String method, HttpServletRequest req) {

               PathMatcher pathMatcher = new AntPathMatcher();
               
               if (Objeto.notBlank(inboundURL) && !isHostValidToInboundURL(req, inboundURL)) {
                    
                    return false;
               }
               
               if (Objeto.notBlank(pathsNotAllowed)) {
                    
                    for (String path : pathsNotAllowed) {

                         String uri = req.getRequestURI();
                         if (Objeto.notBlank(uri) && StringUtils.endsWith(uri, "/")) {
                              
                              uri = StringUtils.removeEnd(uri.trim(), "/");
                         }
                         if (pathMatcher.match(path, uri)) {

                              return false;
                         }
                    }
               }

               if (Objeto.notBlank(pathsAllowed)) {
                    
                    for (String path : pathsAllowed) {
                         
                         if (req.getRequestURI().contains(path)) {
                              
                              return true;
                         }
                    }
               }

               return false;
          }

     },

     RESOURCE {

          @Override
          public boolean filter(Set<String> pathsAllowed, Set<String> pathsNotAllowed, String inboundURL, String method, HttpServletRequest req) {
               
               
               if (Objeto.notBlank(inboundURL) && !isHostValidToInboundURL(req, inboundURL)) {
                    return false;
               }
               
               PathMatcher pathMatcher = new AntPathMatcher();
               final String uri = req.getRequestURI();

               if (Objeto.notBlank(pathsNotAllowed)) {
                    
                    for (String path : pathsNotAllowed) {
                         
                         String mutableUri = uri;
                         if (Objeto.notBlank(uri) && StringUtils.endsWith(uri, "/")) {
                              
                              mutableUri = StringUtils.removeEnd(uri.trim(), "/");
                         }
                         
                         if (pathMatcher.match(path, mutableUri)) {
                              return false;
                         }
                    }
               }

               if (Objeto.notBlank(pathsAllowed)) {
                    
                    for (String path : pathsAllowed) {
                         
                         String mutableUri = uri;
                         if (Objeto.notBlank(uri) && StringUtils.endsWith(uri, "/")) {
                              mutableUri = StringUtils.removeEnd(uri.trim(), "/");
                         }
                         
                         if (pathMatcher.match(path, mutableUri)) {
                              return true;
                         }
                    }
               }

               return false;
          }

     },

     OPERATION {

          @Override
          public boolean filter(Set<String> pathsAllowed, Set<String> pathsNotAllowed, String inboundURL, String method, HttpServletRequest req) {

               PathMatcher pathMatcher = new AntPathMatcher();
               
               if (!isMethodValidToRequest(req, method)) {
                    
                    return false;
               }
               
               if (Objeto.notBlank(inboundURL) && !isHostValidToInboundURL(req, inboundURL)) {
                    
                    return false;
               }
               
               if (Objeto.notBlank(pathsNotAllowed)) {
                    
                    for (String path : pathsNotAllowed) {
                         
                         String uri = req.getRequestURI();
                         if (Objeto.notBlank(uri) && StringUtils.endsWith(uri, "/")) {
                              
                              uri = StringUtils.removeEnd(uri.trim(), "/");
                         }
                         if (pathMatcher.match(path, uri)) {

                              return false;
                         }
                    }
               }

               if (Objeto.notBlank(pathsAllowed)) {
                    
                    for (String path : pathsAllowed) {
                         
                         String uri = req.getRequestURI();
                         if (Objeto.notBlank(uri) && StringUtils.endsWith(uri, "/")) {
                              
                              uri = StringUtils.removeEnd(uri.trim(), "/");
                         }
                         if (pathMatcher.match(path, uri)) {
                              
                              return true;
                         }
                    }
               }

               return false;
          }

     };     
     
     private static boolean isHostValidToInboundURL(HttpServletRequest req, String inboundURL) {
          
          String host = req.getHeader("Host");                                   
          if (Objeto.isBlank(host)) {
               
               host = req.getHeader("host");
          }
          
          if (Objeto.notBlank(host)) {
               
               if (Objeto.notBlank(inboundURL) && inboundURL.toLowerCase().contains(host.toLowerCase())) {
                    
                    return true;
               }
          } else {
               
               if (Objeto.notBlank(inboundURL) && req.getRequestURL().toString().toLowerCase().contains(inboundURL.toLowerCase())) {
                    
                    return true;
               }
          }
          
          return false;
     }
      
     
     private static boolean isMethodValidToRequest(HttpServletRequest req, String method) {
          
          if (Objeto.notBlank(method)) {
               
               if (req.getMethod().toLowerCase().equals(method.trim().toLowerCase())) {
                    
                    return true;
               }
          }
          
          return false;
     }
     
}
