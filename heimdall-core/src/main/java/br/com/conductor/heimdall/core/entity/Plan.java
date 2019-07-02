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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * This class represents a Plan registered to the system.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
@RedisHash("plan")
public class Plan implements Serializable {
     
     private static final long serialVersionUID = 69029100636149640L;

     @Id
     private String id;
     
     private String name;
     
     private String description;
     
     private Api api;
     
     private LocalDateTime creationDate;

     private boolean defaultPlan;
     
     private Status status;

     private Set<Scope> scopes;

     public void addScope(String id) {
          Scope scope = new Scope();
          scope.setId(id);

          this.scopes.add(scope);
     }

     public void removeScope(String id) {
          Scope scope = new Scope();
          scope.setId(id);

          this.scopes.remove(scope);
     }
}
