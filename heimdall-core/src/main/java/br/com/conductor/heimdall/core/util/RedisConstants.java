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
 * This class holds the Redis Pub/Sub Constants.
 * 
 * @author Marcelo Aguiar Rodrigues
 */
public final class RedisConstants {

     private RedisConstants() { }

     public static final String INTERCEPTORS_ADD = "interceptors:add";
     public static final String INTERCEPTORS_REMOVE = "interceptors:remove";
     public static final String INTERCEPTORS_REFRESH = "interceptors:refresh-all";
     public static final String ROUTES_REFRESH = "routes:refresh";

}
