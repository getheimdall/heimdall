
package br.com.conductor.heimdall.api.environment;

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

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <h1>LdapProperty</h1><br/>
 * 
 * Data class that holds tha Ldap properties.
 *
 * @author Marcos Filho
 *
 */
@Data
@ConfigurationProperties(prefix = "heimdall.security.ldap", ignoreUnknownFields = true)
public class LdapProperty {

     private boolean enabled;

     private String url;

     private String searchBase;

     private String userDn;

     private String password;

     private String userSearchFilter;
}
