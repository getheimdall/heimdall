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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 *  The Access Token is required for a safe connection to be established.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Data
@Table(name = "ACCESS_TOKENS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "code" })
public class AccessToken implements Serializable {

     private static final long serialVersionUID = 4545878585832389442L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;
     
     @Column(name = "CODE", length = 250, nullable = false, unique = true)
     private String code;

     @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
     @JoinColumn(name = "APP_ID")
     @JsonIgnoreProperties({ "accessTokens", "developer" })
     private App app;
     
     @Column(name = "EXPIRED_DATE")
     private LocalDateTime expiredDate;

     @Column(name = "CREATION_DATE", nullable = false)
     private LocalDateTime creationDate;
     
     @ManyToMany
     @LazyCollection(LazyCollectionOption.FALSE)
     @JoinTable(name = "ACCESS_TOKENS_PLANS", 
          joinColumns = @JoinColumn(name = "ACCESS_TOKEN_ID", referencedColumnName = "ID"), 
          inverseJoinColumns = @JoinColumn(name = "PLAN_ID", referencedColumnName = "ID"))
     @JsonIgnoreProperties({ "api" })
     private List<Plan> plans;
     
     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;
     
     @PrePersist
     private void initValuesPersist() {

          status = (status == null) ? Status.ACTIVE : status;

          creationDate = LocalDateTime.now();
     }

}
