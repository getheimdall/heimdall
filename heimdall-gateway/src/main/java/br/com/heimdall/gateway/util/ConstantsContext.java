/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * 
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
package br.com.heimdall.gateway.util;

/**
 * List of context related constants
 */
public final class ConstantsContext {

    private ConstantsContext() { }

    public static final String API_ID = "api-id";
    public static final String API_NAME = "api-name";
    public static final String RESOURCE_ID = "resource-id";
    public static final String OPERATION_ID = "operation-id";
    public static final String OPERATION_PATH = "operation-path";
    public static final String PATTERN = "pattern";
    public static final String CORS_FILTER = "run-cors-post-filter";
    public static final String CIRCUIT_BREAKER_ENABLED = "Circuit-Breaker";
    public static final String ENVIRONMENT_VARIABLES = "environment-variables";

    public static final String CORS_FILTER_DEFAULT = "run-cors-filter-default";
    public static final String CLIENT_ID = "client_id";
    public static final String ACCESS_TOKEN = "access_token";

}
