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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Resource registered to the system.
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("resource")
public class Resource implements Serializable {

     private static final long serialVersionUID = 8482072044625354477L;

     @Id
     private String id;

     @Indexed
     private String name;

     private String description;

     @Indexed
     private String apiId;

     private List<String> operations = new ArrayList<>();

     public void addOperation(String id) {
          this.operations.add(id);
     }

     public void removeOperation(String id) {
          this.operations.remove(id);
     }
}
