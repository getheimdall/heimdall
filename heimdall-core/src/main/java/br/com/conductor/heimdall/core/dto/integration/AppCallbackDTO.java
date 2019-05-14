
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
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * Class is a Data Transfer Object for the AppCallback
 *
 * @author Marcos Filho
 *
 */
@Data
public class AppCallbackDTO implements Serializable {

     private static final long serialVersionUID = -871470971090379647L;

     @NotNull
     @Size(max = 180)
     private String name;
     
     @NotNull
     private String code;

     @Size(max = 200)
     private String description;
     
     @NotNull
     private String developer;
}
