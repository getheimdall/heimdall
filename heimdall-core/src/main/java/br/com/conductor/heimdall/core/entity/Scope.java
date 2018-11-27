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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a Scope registered to the system.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@Table(name = "SCOPES")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
public class Scope implements Serializable {

	private static final long serialVersionUID = 7495733828659838366L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 180, unique = true, nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "API_ID", nullable = false)
    private Api api;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SCOPES_OPERATIONS",
            joinColumns = @JoinColumn(name = "SCOPE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "OPERATION_ID", referencedColumnName = "ID"))
    @JsonIgnoreProperties({"resource"})
    private Set<Operation> operations;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, mappedBy="scopes")
    private Set<Plan> plans;

    @JsonIgnore
    public Set<Long> getOperationsIds() {
        return this.operations != null ? this.operations.stream().map(Operation::getId).collect(Collectors.toSet()) : Collections.EMPTY_SET;
    }

    @PreRemove
    private void removeFromPlans() {
        this.plans.forEach(plan -> plan.removeScope(this));
    }

    public void removeOperation(Operation operation) {
        this.operations.remove(operation);
    }

}
