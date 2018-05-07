
package br.com.conductor.heimdall.core.dto;

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

import javax.validation.constraints.NotNull;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import lombok.Data;

/**
 * Class is a Data Transfer Object for the {@link HttpMethod}.
 *
 * @author Filipe Germano
 *
 */
@Data
public class HttpMethodDTO implements Serializable {

     private static final long serialVersionUID = 2727008253508176582L;
     
     @NotNull
     private HttpMethod value;

}
