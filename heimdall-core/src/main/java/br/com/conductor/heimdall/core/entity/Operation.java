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
package br.com.conductor.heimdall.core.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a Operation registered to the system.
 *
 * @author Filipe Germano
 *
 */
@Data
@Table(name = "OPERATIONS", uniqueConstraints = { @UniqueConstraint(columnNames = { "RESOURCE_ID", "METHOD", "PATH" }) })
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Operation implements Serializable {

     private static final long serialVersionUID = -7728017075091653564L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;

     @Column(name = "METHOD", length = 20, nullable = false)
     @Enumerated(EnumType.STRING)
     private HttpMethod method;

     @Column(name = "PATH", length = 180, nullable = false)
     private String path;

     @Column(name = "DESCRIPTION", length = 200)
     private String description;

     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "RESOURCE_ID", nullable = false)
     @JsonManagedReference
     private Resource resource;

     @JsonIgnore
     @ManyToMany(fetch = FetchType.EAGER, mappedBy="operations")
     private Set<Scope> scopes;

     /**
      * Adjust the path to not permit the save or update with "/" or spaces in the end of path.
      */
     @PreUpdate
     @PrePersist
     private void fixBasePath() {
          this.path = this.path.trim();
          if (this.path.endsWith(ConstantsPath.PATH_ROOT)) {
               this.path = StringUtils.removeEnd(path, ConstantsPath.PATH_ROOT);
          }
     }

     @JsonIgnore
     public Set<Long> getScopesIds() {
          return this.scopes != null ? this.scopes.stream().map(Scope::getId).collect(Collectors.toSet()) : Collections.EMPTY_SET;
     }

     @PreRemove
     private void removeFromScopes() {
          scopes.forEach(scope -> scope.removeOperation(this));
     }
}
