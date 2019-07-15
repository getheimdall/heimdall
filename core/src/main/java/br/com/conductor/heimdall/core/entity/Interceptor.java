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
package br.com.conductor.heimdall.core.entity;

import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.InterceptorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Interceptor registered to the system.
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@EqualsAndHashCode(of = { "id" })
@RedisHash("interceptor")
public class Interceptor implements Serializable {
     
     private static final long serialVersionUID = -7481788971695694259L;

     @Id
     private String id;
     
     private String name;
     
     private String description;
     
     private InterceptorType type;

     private InterceptorLifeCycle lifeCycle;

     private Integer order;
     
     private TypeExecutionPoint executionPoint;

     private String content;
     
     private LocalDateTime creationDate;

     private Set<String> ignoredOperations = new HashSet<>();

     @JsonIgnore
     @Indexed
     private String apiId;

     @JsonIgnore
     @Indexed
     private String planId;

     @JsonIgnore
     @Indexed
     private String resourceId;

     @JsonIgnore
     @Indexed
     private String operationId;

     private String referenceId;

     private Boolean status;

}
