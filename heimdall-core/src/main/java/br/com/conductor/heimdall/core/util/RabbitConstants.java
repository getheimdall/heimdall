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
package br.com.conductor.heimdall.core.util;

/**
 * This class holds the Rabbit Constants. 
 * 
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
public final class RabbitConstants {

     private RabbitConstants() { }

     //EXCHANGES
     public static final String EXCHANGE_FANOUT_HEIMDALL_REMOVE_INTERCEPTORS = "heimdall.EXCHANGE_FANOUT_HEIMDALL_REMOVE_INTERCEPTORS";
     public static final String EXCHANGE_FANOUT_HEIMDALL_ADD_INTERCEPTORS = "heimdall.EXCHANGE_FANOUT_HEIMDALL_ADD_INTERCEPTORS";
     public static final String EXCHANGE_FANOUT_HEIMDALL_REFRESH_ALL_INTERCEPTORS = "heimdall.EXCHANGE_FANOUT_HEIMDALL_REFRESH_ALL_INTERCEPTORS";
     public static final String EXCHANGE_FANOUT_HEIMDALL_CLEAN_ALL_CACHES = "heimdall.EXCHANGE_FANOUT_HEIMDALL_CLEAN_ALL_CACHES";
     public static final String EXCHANGE_FANOUT_HEIMDALL_ROUTES = "heimdall.EXCHANGE_FANOUT_HEIMDALL_ROUTES";
     public static final String EXCHANGE_FANOUT_HEIMDALL_MIDDLEWARES = "heimdall.EXCHANGE_FANOUT_HEIMDALL_MIDDLEWARES";
     public static final String EXCHANGE_FANOUT_HEIMDALL_REMOVE_MIDDLEWARES = "heimdall.EXCHANGE_FANOUT_HEIMDALL_REMOVE_MIDDLEWARES";
     public static final String EXCHANGE_FANOUT_HEIMDALL_CLEAN_INTERCEPTORS_CACHE = "heimdall.EXCHANGE_FANOUT_HEIMDALL_CLEAN_INTERCEPTORS_CACHE";

     //QUEUE's
     public static final String QUEUE_HEIMDALL_REMOVE_INTERCEPTORS = "heimdall.QUEUE_REMOVE_INTERCEPTORS";
     public static final String QUEUE_HEIMDALL_INTERCEPTORS = "heimdall.QUEUE_INTERCEPTORS";
     public static final String QUEUE_HEIMDALL_REFRESH_INTERCEPTORS = "heimdall.QUEUE_REFRESH_INTERCEPTORS";
     public static final String QUEUE_HEIMDALL_CACHES_CLEAN = "heimdall.QUEUE_CACHES_CLEAN";
     public static final String QUEUE_HEIMDALL_ROUTES = "heimdall.QUEUE_ROUTES";
     public static final String QUEUE_HEIMDALL_MIDDLEWARES = "heimdall.QUEUE_MIDDLEWARES";
     public static final String QUEUE_HEIMDALL_REMOVE_MIDDLEWARES = "heimdall.QUEUE_REMOVE_MIDDLEWARES";
     public static final String QUEUE_HEIMDALL_CLEAN_INTERCEPTORS_CACHE = "heimdall.QUEUE_CLEAN_INTERCEPTORS_CACHE";

     //Listeners
     public static final String LISTENER_HEIMDAL_ROUTES = "${heimdall.queue.routes}";
     public static final String LISTENER_HEIMDALL_INTERCEPTORS = "${heimdall.queue.interceptors}";
     public static final String LISTENER_HEIMDALL_REMOVE_INTERCEPTORS = "${heimdall.queue.interceptors-remove}";
     public static final String LISTENER_HEIMDALL_CLEAN_CACHE = "${heimdall.queue.caches-clean}";
     public static final String LISTENER_HEIMDALL_REFRESH_INTERCEPTORS = "${heimdall.queue.refresh-interceptors}";
     public static final String LISTENER_HEIMDALL_MIDDLEWARES = "${heimdall.queue.middlewares}";
     public static final String LISTENER_HEIMDALL_REMOVE_MIDDLEWARES = "${heimdall.queue.remove-middlewares}";
     public static final String LISTENER_HEIMDALL_CLEAN_INTERCEPTORS_CACHE = "${heimdall.queue.clean-interceptors-cache}";
}
