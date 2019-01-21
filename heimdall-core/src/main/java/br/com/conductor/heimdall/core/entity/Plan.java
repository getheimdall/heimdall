
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
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.conductor.heimdall.core.enums.Status;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a Plan registered to the system.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
@Table(name = "PLANS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
public class Plan implements Serializable {
     
     private static final long serialVersionUID = 69029100636149640L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;
     
     @Column(name = "NAME", length = 180, nullable = false, unique = true)
     private String name;
     
     @Column(name = "DESCRIPTION", length = 200)
     private String description;
     
     @ManyToOne
     @JoinColumn(name = "API_ID", nullable = false)
     @JsonIgnoreProperties({ "environments" })
     private Api api;
     
     @Column(name = "CREATION_DATE", nullable = false, updatable=false)
     private LocalDateTime creationDate;

     @Column(name = "DEFAULT_PLAN", nullable = false)
     private boolean defaultPlan;
     
     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;

     @ManyToMany(fetch = FetchType.EAGER)
     @JoinTable(name = "SCOPES_PLANS",
             joinColumns = @JoinColumn(name = "PLAN_ID", referencedColumnName = "ID"),
             inverseJoinColumns = @JoinColumn(name = "SCOPE_ID", referencedColumnName = "ID"))
     @JsonIgnoreProperties({"plans"})
     private Set<Scope> scopes;

     @PrePersist
     private void initValuesPersist() {

          if (Objeto.isBlank(status)) {

               status = Status.ACTIVE;
          }
          creationDate = LocalDateTime.now();
     }

     /**
      * Removes a Scope from a Plan
      * @param scope {@link Scope} to be removed
      */
     public void removeScope(Scope scope) {
          this.scopes.remove(scope);
     }

}
