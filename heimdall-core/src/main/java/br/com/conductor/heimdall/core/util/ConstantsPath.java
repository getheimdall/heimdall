
package br.com.conductor.heimdall.core.util;

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
 * This class holds the path constants.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
public class ConstantsPath {
	
     public static final String PATH_ROOT = "/";
     
     public static final String PATH_API = "/v1/api";
     
     public static final String PATH_HEIMDALL_PATTERN = "/heimdall/**";

     public static final String PATH_MANAGER_PATTERN = "/manager/**";
     
     public static final String PATH_APIS = PATH_API + "/apis";

     public static final String PATH_APPS = PATH_API + "/apps";

     public static final String PATH_DEVELOPERS = PATH_API + "/developers";

     public static final String PATH_HTTP_METHODS = PATH_API + "/http-methods";

     public static final String PATH_PLANS = PATH_API + "/plans";

     public static final String PATH_RESOURCES = PATH_APIS + "/{apiId}" + "/resources";

     public static final String PATH_MIDDLEWARES = PATH_APIS + "/{apiId}" + "/middlewares";
     
     public static final String PATH_OPERATIONS = PATH_RESOURCES + "/{resourceId}" + "/operations";

     public static final String PATH_ACCESS_TOKENS = PATH_API + "/access_tokens";

     public static final String PATH_INTERCEPTORS = PATH_API + "/interceptors";
     
     public static final String PATH_INTEGRATION_RESOURCES = PATH_API + "/integrations";

     public static final String PATH_ENVIRONMENTS = PATH_API + "/environments";

     public static final String PATH_CACHES = PATH_API + "/caches";
     
     public static final String PATH_USERS = PATH_API + "/users";

     public static final String PATH_ROLES = PATH_API + "/roles";
     
     public static final String PATH_PRIVILEGES = PATH_API + "/privileges";
     
     public static final String PATH_PROVIDER = PATH_API + "/providers";
     
     public static final String PATH_TRACES = PATH_API + "/traces";

     public static final String PATH_METRICS = PATH_API + "/metrics";

}
