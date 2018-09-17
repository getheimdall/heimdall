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
package br.com.conductor.heimdall.core.dto;

import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Class is a Data Transfer Object for the {@link Developer}.
 *
 * @author Filipe Germano
 *
 */
@Data
public class DeveloperDTO implements Serializable {

     private static final long serialVersionUID = 4498108219434718793L;

     @NotNull
     @Size(max = 180)
     private String name;
     
     @NotNull
     @Size(max = 180)
     private String email;
     
     private Status status;

}
