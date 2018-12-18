
package br.com.conductor.heimdall.core.dto.logs;

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

import java.io.Serializable;

import br.com.conductor.heimdall.core.entity.Trace;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import lombok.Data;

/**
 * This is a Data Transfer Object for the {@link Trace}
 * 
 * @author Marcelo Aguiar
 *
 */
@Data
public class TraceDTO implements Serializable {
	
	private static final long serialVersionUID = -8264906278477847182L;

	private HttpMethod method;
	
	private Integer resultStatus;
	
	private String url;
	
	private String insertedOnDate;
	
}
