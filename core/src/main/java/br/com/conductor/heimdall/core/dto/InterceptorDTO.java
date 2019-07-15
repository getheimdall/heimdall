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

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.InterceptorType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * Class is a Data Transfer Object for the {@link Interceptor}.
 *
 * @author Filipe Germano
 *
 */
@Data
public class InterceptorDTO implements Serializable {
     
     private static final long serialVersionUID = -2075622644106420623L;

     @NotNull
     @Size(max = 80)
     private String name;
     
     @Size(max = 200)
     private String description;
     
     @NotNull
     private InterceptorType type;
     
     @NotNull
     private Integer order;

     @Size(max = 4000)
     private String content;
     
     @NotNull
     private InterceptorLifeCycle lifeCycle;
     
     @NotNull
     private TypeExecutionPoint executionPoint;

     @NotNull
     private String referenceId;

     private Set<String> ignoredOperations;
     
     private String apiId;

     private Boolean status;
     
}
