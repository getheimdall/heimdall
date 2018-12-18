
package br.com.conductor.heimdall.gateway.trace;

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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * Provides thread safe methods to manage the Trace.
 *
 * @author Thiago Sampaio
 * @author Filipe Germano
 * @author Fabio Sicupira
 * @author Marcelo Aguiar Rodrigues
 *
 */
@Slf4j
public class TraceContextHolder {

     private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

     private static final ConcurrentHashMap<String, Trace> traceMap = new ConcurrentHashMap<>();

     /**
      * Implementation of the Initialization-on-demand holder idiom.
      * 
      * @author Marcelo Aguiar Rodrigues
      * @see <a href="http://literatejava.com/jvm/fastest-threadsafe-singleton-jvm/">Fastest Thread-safe Singleton in Java</a>
      * 
      */
     private static class LazyHolder {
          static final TraceContextHolder INSTANCE = new TraceContextHolder();
     }

     /**
      * Thread safe singleton initializer.
      * 
      * @return		{@link TraceContextHolder} instance
      */
     public static TraceContextHolder getInstance() {

          return LazyHolder.INSTANCE;
     }

     /**
      * Initializes a {@link Trace}
      * 
      * @param printAllTrace	boolean
      * @param profile			Profile
      * @param request			{@link ServletRequest}
      * @return					{@link Trace}
      */
     public Trace init(boolean printAllTrace, String profile, ServletRequest request, boolean printMongo) {
          String uuid = UUID.randomUUID().toString();
          contextHolder.set(uuid);
          traceMap.put(uuid, new Trace(printAllTrace, profile, request, printMongo));
          
          log.debug("Initializing TraceContext with ID: {}", uuid);
          return getActualTrace();

     }

     /**
      * Returns the actual {@link Trace}.
      * 
      * @return {@link Trace}, null if the context is null
      */
     public Trace getActualTrace() {
          
          if (contextHolder.get() == null) {
               
               return null;
          } else {
               
               return traceMap.get(contextHolder.get());
          }
     }
     
     /**
      * Clears actual trace.
      */
     public void clearActual() {
          
          if (contextHolder.get() != null) {
               
               traceMap.remove(contextHolder.get());
          }
     }
     
     /**
      * Remove current value.
      */
     public void unset() {
          
          contextHolder.remove();
      }

}
