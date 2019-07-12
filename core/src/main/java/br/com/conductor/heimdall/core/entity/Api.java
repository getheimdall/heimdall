/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.core.entity;

import br.com.conductor.heimdall.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Api registered to the system.<br/>
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("api")
public class Api implements Serializable {

     private static final long serialVersionUID = 1817533065623403784L;

     @Id
     private String id;

     private String name;

     private String version;

     private String description;

     @Indexed
     private String basePath;

     private boolean cors;

     private LocalDateTime creationDate;

     private Set<String> resources = new HashSet<>();

     private Status status;

     private Set<String> environments = new HashSet<>();
     
     private Set<String> plans = new HashSet<>();

     public void addResource(String id) {
          this.resources.add(id);
     }

     public void removeResource(String id) {
          this.resources.remove(id);
     }

     public void addPlan(String id) {
          this.plans.add(id);
     }

     public void removePlan(String id) {
          this.plans.remove(id);
     }

}
