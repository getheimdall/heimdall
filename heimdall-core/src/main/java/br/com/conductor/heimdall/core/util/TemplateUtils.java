
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
 * <h1>TemplateUtils</h1><br/>
 * 
 * This class provides templates for Ratting, AccessToken and Mock.
 * 
 * @author Filipe Germano
 *
 */
public class TemplateUtils {

     public static final String TEMPLATE_RATTING = "{\"calls\":20,\"interval\":\"MINUTES\"}";
     public static final String TEMPLATE_ACCESS_TOKEN = "{\"location\": \"HEADER\", \"name\": \"access_token\"}";
     public static final String TEMPLATE_MOCK = "{\"body\": \"Example Mock\", \"status\": \"200\"}";

}
