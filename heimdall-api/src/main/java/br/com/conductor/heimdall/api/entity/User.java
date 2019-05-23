/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
package br.com.conductor.heimdall.api.entity;

import br.com.conductor.heimdall.api.enums.TypeUser;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data class that represents the User.
 *
 * @author Marcos Filho
 *
 */
@Data
@Table(name = "USERS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

     private static final long serialVersionUID = -7740868543851971847L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;

     @Column(name = "FIRST_NAME", length = 80, nullable = false)
     private String firstName;

     @Column(name = "LAST_NAME", length = 80, nullable = false)
     private String lastName;
     
     @Column(name = "USERNAME", length = 30, nullable = false, unique = true)
     private String userName;

     @Column(name = "EMAIL", length = 150, nullable = false, unique = true)
     private String email;

     @Column(name = "PASSWORD", length = 300, nullable = false)
     private String password;

     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;
     
     @Column(name = "CREATION_DATE", nullable = false)
     private LocalDateTime creationDate;
     
     @Column(name = "TYPE_USER", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private TypeUser type;
     
     @ManyToMany(fetch = FetchType.LAZY)
     @JoinTable(name = "USERS_ROLES", 
               joinColumns = @JoinColumn(name = "USER_ID", referencedColumnName = "id"), 
               inverseJoinColumns = @JoinColumn(name = "ROLE_ID", referencedColumnName = "id"))
     private Set<Role> roles;
     
     @PrePersist
     private void initValuesPersist() {

          status = status == null ? Status.ACTIVE : status;
          
          creationDate = LocalDateTime.now();        
     }
}
