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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents a Variable registered to the system.
 * 
 * @author Filipe Germano
 *
 */
@Data
@Table(name = "VARIABLES", uniqueConstraints = { @UniqueConstraint(columnNames = { "ENVIRONMENT_ID", "[KEY]" }) })
@Entity
@DynamicUpdate
@DynamicInsert
@EqualsAndHashCode(of = { "id" })
public class Variable implements Serializable {
     
     private static final long serialVersionUID = -5575544928960511350L;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "ID")
     private Long id;

     @Column(name = "[KEY]", length = 180, nullable = false)
     private String key;
     
     @Column(name = "VALUE", length = 250, nullable = false)
     private String value;

     @ManyToOne
     @JoinColumn(name = "ENVIRONMENT_ID", nullable = false)     
     private Environment environment;     
     
}
