
package br.com.conductor.heimdall.core.entity;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a Interceptor registered to the system.
 * 
 * @author Filipe Germano
 *
 */
@Data
@Table(name = "INTERCEPTORS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
public class Interceptor implements Serializable {
     
     private static final long serialVersionUID = -7481788971695694259L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;
     
     @Column(name = "NAME", length = 80, nullable = false)
     private String name;
     
     @Column(name = "DESCRIPTION", length = 200)
     private String description;
     
     @Column(name = "TYPE", length = 20, nullable = false)
     @Enumerated(EnumType.STRING)
     private TypeInterceptor type;

     @Column(name = "LIFE_CYCLE", length = 20, nullable = false)
     @Enumerated(EnumType.STRING)
     private InterceptorLifeCycle lifeCycle;

     @Column(name = "EXECUTION_ORDER", nullable = false)
     private Integer order;
     
     @Column(name = "EXECUTION_POINT", length = 20, nullable = false)
     @Enumerated(EnumType.STRING)
     private TypeExecutionPoint executionPoint;

     @ManyToOne
     @JoinColumn(name = "ENVIRONMENT_ID")
     private Environment environment;

     @Column(name = "CONTENT", length = 4000)
     private String content;
     
     @Column(name = "CREATION_DATE", nullable = false)
     private LocalDateTime creationDate;
     
     @ManyToMany
     @LazyCollection(LazyCollectionOption.FALSE)
     @JoinTable(name = "IGNORED_INTERCEPTORS_RESOURCES", 
     joinColumns = @JoinColumn(name = "INTERCEPTOR_ID", referencedColumnName = "ID"), 
     inverseJoinColumns = @JoinColumn(name = "RESOURCE_ID", referencedColumnName = "ID"))
     private List<Resource> ignoredResources;

     @ManyToMany
     @LazyCollection(LazyCollectionOption.FALSE)
     @JoinTable(name = "IGNORED_INTERCEPTORS_OPERATIONS", 
          joinColumns = @JoinColumn(name = "INTERCEPTOR_ID", referencedColumnName = "ID"), 
          inverseJoinColumns = @JoinColumn(name = "OPERATION_ID", referencedColumnName = "ID"))
     private List<Operation> ignoredOperations;
     
     @JsonIgnore
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "PLAN_ID")
     private Plan plan;

     @JsonIgnore
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "RESOURCE_ID")
     private Resource resource;

     @JsonIgnore
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "OPERATION_ID")
     private Operation operation;
     
     @JsonIgnore
     @ManyToMany
     @JoinTable(name = "MIDDLEWARES_INTERCEPTORS", 
          joinColumns = @JoinColumn(name = "INTERCEPTOR_ID", referencedColumnName = "ID"), 
          inverseJoinColumns = @JoinColumn(name = "MIDDLEWARE_ID", referencedColumnName = "ID"))
     private List<Middleware> middlewares;
     
     @Transient
     private Long referenceId;
     
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "API_ID")
     @JsonIgnore
     private Api api;
     
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

     public Set<Long> getIgnoredResourcesIds() {
          return ignoredResources.stream().map(Resource::getId).collect(Collectors.toSet());
     }

     public Set<Long> getIgnoredOperationsIds() {
          return ignoredOperations.stream().map(Operation::getId).collect(Collectors.toSet());
     }
     
}
