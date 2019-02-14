
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
 * This class provides templates for Ratting, AccessToken, Mock, OAuth and Cache.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
public class TemplateUtils {

     public static final String TEMPLATE_RATTING = "{\"calls\":20,\"interval\":\"MINUTES\"}";
     public static final String TEMPLATE_ACCESS_TOKEN = "{\"location\": \"HEADER\", \"name\": \"access_token\"}";
     public static final String TEMPLATE_MOCK = "{\"body\": \"Example Mock\", \"status\": \"200\"}";
     public static final String TEMPLATE_OAUTH = "{\"providerId\": 1 , \"typeOAuth\":\"GENERATE\", \"timeAccessToken\":20, \"timeRefreshToken\":1800, \"privateKey\":\"privateKey\" }";
     public static final String TEMPLATE_BLOCK_IPS = "{\"ips\": [ \"127.0.0.0\", \"127.0.0.1\" ]}";
     public static final String TEMPLATE_CACHE = "{\"cache\":\"cache-name\", \"timeToLive\": 10000, \"headers\": [\"header1\", \"header2\"], \"queryParams\": [\"queryParam1\", \"queryParam2\"]}";
     public static final String TEMPLATE_CACHE_CLEAR = "{\"cache\":\"cache-name\"}";
     public static final String TEMPLATE_LOG_MASKER = "{\"body\":true,\"uri\":true,\"headers\":true,\"ignoredHeaders\":[\"headerName\"]}";
     public static final String TEMPLATE_CORS = "{\"Access-Control-Allow-Origin\": \"*\", " +
             "\"Access-Control-Allow-Credentials\": \"true\", " +
             "\"Access-Control-Allow-Methods\": \"POST, GET, PUT, PATCH, DELETE, OPTIONS\", " +
             "\"Access-Control-Allow-Headers\": \"origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential\", " +
             "\"Access-Control-Max-Age\": \"3600\"}";
}
