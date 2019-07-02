/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.core.dto;

import java.io.Serializable;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

/**
 * Class is a Data Transfer Object for the {@link Environment}.
 *
 * @author Filipe Germano
 *
 */
@Data
public class EnvironmentDTO implements Serializable {

     private static final long serialVersionUID = 56759386045399870L;

     @NotNull
     @Size(max = 180)
     private String name;

     @NotNull
     @Size(max = 200)
     private String description;

     @NotNull
     @Size(max = 250)
     private String inboundURL;

     @NotNull
     @Size(max = 250)
     private String outboundURL;
     
     private Status status;

     private Map<String, String> variables;
     
}
