
package br.com.conductor.heimdall.api.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
 * Class that holds the Privilege constants.
 *
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * 
 */
public class ConstantsPrivilege {
     
     public static final String PRIVILEGE_READ_ACCESSTOKEN = "hasAuthority('READ_ACCESSTOKEN')";
     public static final String PRIVILEGE_CREATE_ACCESSTOKEN = "hasAuthority('CREATE_ACCESSTOKEN')";
     public static final String PRIVILEGE_UPDATE_ACCESSTOKEN = "hasAuthority('UPDATE_ACCESSTOKEN')";
     public static final String PRIVILEGE_DELETE_ACCESSTOKEN = "hasAuthority('DELETE_ACCESSTOKEN')";
     
     public static final String PRIVILEGE_READ_API = "hasAuthority('READ_API')";
     public static final String PRIVILEGE_CREATE_API = "hasAuthority('CREATE_API')";
     public static final String PRIVILEGE_UPDATE_API = "hasAuthority('UPDATE_API')";
     public static final String PRIVILEGE_DELETE_API = "hasAuthority('DELETE_API')";

     public static final String PRIVILEGE_READ_MIDDLEWARE = "hasAuthority('READ_MIDDLEWARE')";
     public static final String PRIVILEGE_CREATE_MIDDLEWARE = "hasAuthority('CREATE_MIDDLEWARE')";
     public static final String PRIVILEGE_UPDATE_MIDDLEWARE = "hasAuthority('UPDATE_MIDDLEWARE')";
     public static final String PRIVILEGE_DELETE_MIDDLEWARE = "hasAuthority('DELETE_MIDDLEWARE')";

     public static final String PRIVILEGE_READ_APP = "hasAuthority('READ_APP')";
     public static final String PRIVILEGE_CREATE_APP = "hasAuthority('CREATE_APP')";
     public static final String PRIVILEGE_UPDATE_APP = "hasAuthority('UPDATE_APP')";
     public static final String PRIVILEGE_DELETE_APP = "hasAuthority('DELETE_APP')";
     
     public static final String PRIVILEGE_READ_CACHES = "hasAuthority('READ_CACHES')";
     public static final String PRIVILEGE_DELETE_CACHES = "hasAuthority('DELETE_CACHES')";
     
     public static final String PRIVILEGE_READ_DEVELOPER = "hasAuthority('READ_DEVELOPER')";
     public static final String PRIVILEGE_CREATE_DEVELOPER = "hasAuthority('CREATE_DEVELOPER')";
     public static final String PRIVILEGE_UPDATE_DEVELOPER = "hasAuthority('UPDATE_DEVELOPER')";
     public static final String PRIVILEGE_DELETE_DEVELOPER = "hasAuthority('DELETE_DEVELOPER')";
     
     public static final String PRIVILEGE_READ_ENVIRONMENT = "hasAuthority('READ_ENVIRONMENT')";
     public static final String PRIVILEGE_CREATE_ENVIRONMENT = "hasAuthority('CREATE_ENVIRONMENT')";
     public static final String PRIVILEGE_UPDATE_ENVIRONMENT = "hasAuthority('UPDATE_ENVIRONMENT')";
     public static final String PRIVILEGE_DELETE_ENVIRONMENT = "hasAuthority('DELETE_ENVIRONMENT')";
     
     public static final String PRIVILEGE_READ_INTERCEPTOR = "hasAuthority('READ_INTERCEPTOR')";
     public static final String PRIVILEGE_CREATE_INTERCEPTOR = "hasAuthority('CREATE_INTERCEPTOR')";
     public static final String PRIVILEGE_UPDATE_INTERCEPTOR = "hasAuthority('UPDATE_INTERCEPTOR')";
     public static final String PRIVILEGE_DELETE_INTERCEPTOR = "hasAuthority('DELETE_INTERCEPTOR')";
     public static final String PRIVILEGE_REFRESH_INTERCEPTOR = "hasAuthority('REFRESH_INTERCEPTOR')";
     
     public static final String PRIVILEGE_READ_OPERATION = "hasAuthority('READ_OPERATION')";
     public static final String PRIVILEGE_CREATE_OPERATION = "hasAuthority('CREATE_OPERATION')";
     public static final String PRIVILEGE_UPDATE_OPERATION = "hasAuthority('UPDATE_OPERATION')";
     public static final String PRIVILEGE_DELETE_OPERATION = "hasAuthority('DELETE_OPERATION')";
     public static final String PRIVILEGE_REFRESH_OPERATION = "hasAuthority('REFRESH_OPERATION')";
     
     public static final String PRIVILEGE_READ_PLAN = "hasAuthority('READ_PLAN')";
     public static final String PRIVILEGE_CREATE_PLAN = "hasAuthority('CREATE_PLAN')";
     public static final String PRIVILEGE_UPDATE_PLAN = "hasAuthority('UPDATE_PLAN')";
     public static final String PRIVILEGE_DELETE_PLAN = "hasAuthority('DELETE_PLAN')";
     
     public static final String PRIVILEGE_READ_PRIVILEGE = "hasAuthority('READ_PRIVILEGE')";
     
     public static final String PRIVILEGE_READ_RESOURCE = "hasAuthority('READ_RESOURCE')";
     public static final String PRIVILEGE_CREATE_RESOURCE = "hasAuthority('CREATE_RESOURCE')";
     public static final String PRIVILEGE_UPDATE_RESOURCE = "hasAuthority('UPDATE_RESOURCE')";
     public static final String PRIVILEGE_DELETE_RESOURCE = "hasAuthority('DELETE_RESOURCE')";
     public static final String PRIVILEGE_REFRESH_RESOURCE = "hasAuthority('REFRESH_RESOURCE')";
     
     public static final String PRIVILEGE_READ_ROLE = "hasAuthority('READ_ROLE')";
     public static final String PRIVILEGE_CREATE_ROLE = "hasAuthority('CREATE_ROLE')";
     public static final String PRIVILEGE_UPDATE_ROLE = "hasAuthority('UPDATE_ROLE')";
     public static final String PRIVILEGE_DELETE_ROLE = "hasAuthority('DELETE_ROLE')";
     
     public static final String PRIVILEGE_READ_USER = "hasAuthority('READ_USER')";
     public static final String PRIVILEGE_CREATE_USER = "hasAuthority('CREATE_USER')";
     public static final String PRIVILEGE_UPDATE_USER = "hasAuthority('UPDATE_USER')";
     public static final String PRIVILEGE_DELETE_USER = "hasAuthority('DELETE_USER')";
     
     public static final String PRIVILEGE_READ_PROVIDER = "hasAuthority('READ_PROVIDER')";
     public static final String PRIVILEGE_CREATE_PROVIDER = "hasAuthority('CREATE_PROVIDER')";
     public static final String PRIVILEGE_UPDATE_PROVIDER = "hasAuthority('UPDATE_PROVIDER')";
     public static final String PRIVILEGE_DELETE_PROVIDER = "hasAuthority('DELETE_PROVIDER')";
     
     public static final String PRIVILEGE_READ_TRACES = "hasAuthority('READ_TRACES')";
     public static final String PRIVILEGE_CREATE_TRACES = "hasAuthority('CREATE_TRACES')";
     public static final String PRIVILEGE_UPDATE_TRACES = "hasAuthority('UPDATE_TRACES')";
     public static final String PRIVILEGE_DELETE_TRACES = "hasAuthority('DELETE_TRACES')";

     public static final String PRIVILEGE_READ_METRICS = "hasAuthority('READ_METRICS')";

}
