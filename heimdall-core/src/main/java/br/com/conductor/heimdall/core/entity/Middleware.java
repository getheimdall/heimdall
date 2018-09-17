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

import br.com.conductor.heimdall.core.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.github.thiagonego.alfred.object.Objeto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class represents a Middleware registered to the system.
 * 
 * @author Filipe Germano
 *
 */
@Data
@Table(name = "MIDDLEWARES")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
public class Middleware implements Serializable {

     private static final long serialVersionUID = -7787479865447488433L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;
     
     @Column(name = "NAME", length = 80, nullable = false)
     private String name;
     
     @Column(name = "VERSION", length = 20, nullable = false)
     private String version;
     
     @Column(name = "PATH", length = 255, nullable = false)
     private String path;    

     @Column(name = "TYPE", length = 20, nullable = false)
     private String type;

     @Column(name = "[FILE]")
     @JsonIgnore
     private byte[] file;
     
     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "API_ID", nullable = false)
     @JsonManagedReference
     private Api api;
     
     @Column(name = "CREATION_DATE", nullable = false)
     private LocalDateTime creationDate;
     
     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;
     
     @JsonIgnore
     @ManyToMany(fetch = FetchType.EAGER)
     @JoinTable(name = "MIDDLEWARES_INTERCEPTORS", 
          joinColumns = @JoinColumn(name = "MIDDLEWARE_ID", referencedColumnName = "ID"), 
          inverseJoinColumns = @JoinColumn(name = "INTERCEPTOR_ID", referencedColumnName = "ID"))
     private List<Interceptor> interceptors;
     
     @PrePersist
     private void initValuesPersist() {

          if (Objeto.isBlank(status)) {

               status = Status.ACTIVE;
          }
          creationDate = LocalDateTime.now();
          
          name = api.getId().toString() + "." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmss"));
          
     }
     
}
