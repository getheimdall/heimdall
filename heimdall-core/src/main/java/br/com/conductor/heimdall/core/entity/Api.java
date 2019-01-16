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

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a Api registered to the system.<br/>
 * 
 * @author Filipe Germano
 *
 */
@Data
@Table(name = "APIS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
public class Api implements Serializable {

     private static final long serialVersionUID = 1817533065623403784L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;

     @Column(name = "NAME", length = 80, nullable = false)
     private String name;

     @Column(name = "VERSION", length = 40, nullable = false)
     private String version;

     @Column(name = "DESCRIPTION", length = 200)
     private String description;

     @Column(name = "BASE_PATH", length = 80, nullable = false, unique = true)
     private String basePath;

     @Column(name = "CORS", nullable = false)
     private boolean cors;

     @Column(name = "CREATION_DATE", nullable = false)
     @DateTimeFormat(iso = ISO.DATE_TIME)
     @JsonDeserialize(using = LocalDateTimeDeserializer.class)
     @JsonSerialize(using = LocalDateTimeSerializer.class)
     private LocalDateTime creationDate;

     @JsonIgnore
     @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
     @JoinColumn(name = "API_ID")
     private Set<Resource> resources;

     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;

     @JsonIgnore
     @Column(name = "TAGS", length = 2000)
     private String tag;
     
     @Transient
     private List<String> tags;
     
     @ManyToMany
     @LazyCollection(LazyCollectionOption.FALSE)
     @JoinTable(name = "APIS_ENVIRONMENTS", 
          joinColumns = @JoinColumn(name = "API_ID", referencedColumnName = "ID"), 
          inverseJoinColumns = @JoinColumn(name = "ENVIRONMENT_ID", referencedColumnName = "ID"))
     private List<Environment> environments;
     
     @OneToMany(mappedBy = "api", fetch=FetchType.LAZY)
     @JsonIgnore
     private List<Plan> plans;
     
     @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE }, orphanRemoval = true, mappedBy = "resource")
     @JsonIgnore
     private List<Interceptor> interceptors;
     
     @PrePersist
     private void callPrePersists() {
          initValuesPersist();
          fixBasePath();
     }
     
     @PreUpdate
     private void callPreUpdates() {
          fixBasePath();
     }
     
     private void initValuesPersist() {

          if (Objeto.isBlank(status)) {

               status = Status.ACTIVE;
          }
          
          creationDate = LocalDateTime.now();
     }
     
     /*
      * Adjust the basepath to not permit the save or update with "/" in the end of path.
      */
     private void fixBasePath() {

          if (this.basePath.endsWith(ConstantsPath.PATH_ROOT)) {
               this.basePath = StringUtils.removeEnd(basePath, ConstantsPath.PATH_ROOT);
          }
     }     

}
