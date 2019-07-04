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
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Interceptor registered to the system.
 * 
 * @author Filipe Germano
 *
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
     
     private TypeInterceptor type;

     private InterceptorLifeCycle lifeCycle;

     private Integer order;
     
     private TypeExecutionPoint executionPoint;

     private String environmentId;

     private String content;
     
     private LocalDateTime creationDate;

     private Set<Long> ignoredResources = new HashSet<>();

     private Set<Long> ignoredOperations = new HashSet<>();
     
     @JsonIgnore
     private String planId;

     @JsonIgnore
     private String resourceId;

     @JsonIgnore
     private String operationId;

     @Transient
     private String referenceId;
     
     @JsonIgnore
     private String apiId;

     private Boolean status;
     
     private void initValuesPersist() {

          creationDate = LocalDateTime.now();
          
          switch (lifeCycle) {
               case API:
                    apiId = referenceId;
                    break;
               case PLAN:
                    planId = referenceId;
                    break;
               case RESOURCE:
                    resourceId = referenceId;
                    break;
               case OPERATION:
                    operationId = referenceId;
                    break;
               default:
                    break;
          }
     }

     private void initValuesLoad() {
          
          switch (lifeCycle) {
               case API:
                    referenceId = apiId;
                    break;
               case PLAN:
                    referenceId = planId;
                    break;
               case RESOURCE:
                    referenceId = resourceId;
                    break;
               case OPERATION:
                    referenceId = operationId;
                    break;
               default:
                    break;
          }
     }

}
