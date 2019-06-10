
package br.com.conductor.heimdall.core.dto.integration;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class is a Data Transfer Object for the AccessToken.
 *
 * @author Marcos Filho
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AccessTokenDTO implements Serializable {

     private static final long serialVersionUID = 2602974202125716681L;

     @NotNull
     private String code;

     @NotNull
     private AppCallbackDTO app;

     private Status status;
     
}
