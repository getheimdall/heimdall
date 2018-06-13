package br.com.conductor.heimdall.gateway.filter.helper;

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

import java.util.Map;

import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import lombok.Data;

/**
 * Data class that represents a ApiResponse.
 *
 * @author Filipe Germano
 *
 */
@Data
public class ApiResponseImpl implements ApiResponse {

     private static final long serialVersionUID = -5283776526915350208L;

     private String body;

     private Map<String, String> headers;

     private Integer status;

}
