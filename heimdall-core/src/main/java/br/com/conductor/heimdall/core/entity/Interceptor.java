
package br.com.conductor.heimdall.core.entity;

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

import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;
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

     private Environment environment;

     private String content;
     
     private LocalDateTime creationDate;

     private Set<Long> ignoredResources;

     private Set<Long> ignoredOperations;
     
     @JsonIgnore
     private Plan plan;

     @JsonIgnore
     private Resource resource;

     @JsonIgnore
     private Operation operation;

     @Transient
     private String referenceId;
     
     @JsonIgnore
     private Api api;

     private Boolean status;
     
     @PrePersist
     private void initValuesPersist() {

          creationDate = LocalDateTime.now();
          
          switch (lifeCycle) {
               case API:
                    api = new Api();
                    api.setId(referenceId);
                    break;
               case PLAN:
                    plan = new Plan();
                    plan.setId(referenceId);
                    break;
               case RESOURCE:
                    resource = new Resource();
                    resource.setId(referenceId);
                    break;
               case OPERATION:
                    operation = new Operation();
                    operation.setId(referenceId);
                    break;
               default:
                    break;
          }
     }

     @PostLoad
     private void initValuesLoad() {
          
          switch (lifeCycle) {
               case API:
                    referenceId = api.getId();
                    break;
               case PLAN:
                    referenceId = plan.getId();
                    break;
               case RESOURCE:
                    referenceId = resource.getId();
                    break;
               case OPERATION:
                    referenceId = operation.getId();
                    break;
               default:
                    break;
          }
     }

}
