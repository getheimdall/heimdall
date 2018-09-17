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
package br.com.conductor.heimdall.core.entity;

import br.com.conductor.heimdall.core.trace.FilterDetail;
import br.com.conductor.heimdall.core.trace.GeneralTrace;
import br.com.conductor.heimdall.core.trace.RequestResponseParser;
import br.com.conductor.heimdall.core.trace.StackTrace;
import lombok.Data;

import java.util.List;

/**
 * This class represents a request Trace that will be stored for logging.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Data
public class Trace {
	
    private String method;

    private String url;

    private Integer resultStatus;

    private Long durationMillis;

    private String insertedOnDate;

    private Long apiId;

    private String apiName;

    private String app;

    private String accessToken;

    private String receivedFromAddress;

    private String clientId;

    private Long resourceId;

    private String appDeveloper;
    
    private Long operationId;
    
    private RequestResponseParser request;
    
    private RequestResponseParser response;

    private String pattern;
    
    private StackTrace stackTrace;

    private List<GeneralTrace> traces;
    
    private List<FilterDetail> filters;

    private String profile;

}
