
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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.conductor.heimdall.core.enums.Status;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a Developer registered to the system.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
@Table(name = "DEVELOPERS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
public class Developer implements Serializable {

     private static final long serialVersionUID = -5692191776312048169L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;

     @Column(name = "NAME", length = 180, nullable = false)
     private String name;

     @Column(name = "EMAIL", length = 180, nullable = false, unique = true)
     private String email;

     @Column(name = "PASSWORD", length = 300)
     private String password;

     @Column(name = "CREATION_DATE", nullable = false)
     private LocalDateTime creationDate;

     @OneToMany(mappedBy = "developer", fetch=FetchType.LAZY, cascade = { CascadeType.REMOVE })
     @JsonIgnore
     private List<App> apps;

     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;

     @PrePersist
     private void initValuesPersist() {

          if (Objeto.isBlank(status)) {

               status = Status.ACTIVE;
          }
          creationDate = LocalDateTime.now();
     }
}
