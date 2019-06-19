
package br.com.conductor.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.extern.slf4j.Slf4j;

/**
 * This class creates the required system properties that will be needed for the Rabbit Queue.
 * 
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@Slf4j
public final class RabbitQueueUtils {

     private RabbitQueueUtils() { }

     public static void init() {
          try {

               System.setProperty("heimdall.queue.interceptors", RabbitConstants.QUEUE_HEIMDALL_INTERCEPTORS + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.refresh-interceptors", RabbitConstants.QUEUE_HEIMDALL_REFRESH_INTERCEPTORS + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.routes", RabbitConstants.QUEUE_HEIMDALL_ROUTES + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.caches-clean", RabbitConstants.QUEUE_HEIMDALL_CACHES_CLEAN + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.interceptors-remove", RabbitConstants.QUEUE_HEIMDALL_REMOVE_INTERCEPTORS + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.middlewares", RabbitConstants.QUEUE_HEIMDALL_MIDDLEWARES + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.remove-middlewares", RabbitConstants.QUEUE_HEIMDALL_REMOVE_MIDDLEWARES + "." + InetAddress.getLocalHost().getHostName());
               System.setProperty("heimdall.queue.clean-interceptors-cache", RabbitConstants.QUEUE_HEIMDALL_CLEAN_INTERCEPTORS_CACHE + "." + InetAddress.getLocalHost().getHostName());
          } catch (UnknownHostException e) {
               
        	  log.error(e.getMessage(), e);
          }
     }
}
