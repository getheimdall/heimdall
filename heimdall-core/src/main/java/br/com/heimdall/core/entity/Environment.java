
package br.com.heimdall.core.entity;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import br.com.heimdall.core.enums.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a Environment registered to the system.
 * 
 * @author Filipe Germano
 *
 */
@Data
@Table(name = "ENVIRONMENTS")
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
public class Environment implements Serializable {

     private static final long serialVersionUID = 5863767211338151356L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;

     @Column(name = "NAME", length = 180, nullable = false)
     private String name;

     @Column(name = "DESCRIPTION", length = 200)
     private String description;

     @Column(name = "INBOUND_URL", length = 250, nullable = false, unique = true)
     private String inboundURL;

     @Column(name = "OUTBOUND_URL", length = 250, nullable = false)
     private String outboundURL;
     
     @Column(name = "CREATION_DATE", nullable = false)
     private LocalDateTime creationDate;
     
     @Column(name = "STATUS", length = 10, nullable = false)
     @Enumerated(EnumType.STRING)
     private Status status;

     @ElementCollection(fetch = FetchType.EAGER)
     @MapKeyColumn(name = "[KEY]")
     @Column(name = "VALUE")
     @CollectionTable(name = "VARIABLES", joinColumns = @JoinColumn(name = "ENVIRONMENT_ID", referencedColumnName = "ID"))
     private Map<String, String> variables;
     
     @PrePersist
     private void initValuesPersist() {

          status = (status == null) ? Status.ACTIVE : status;

          creationDate = LocalDateTime.now();
     }

}
