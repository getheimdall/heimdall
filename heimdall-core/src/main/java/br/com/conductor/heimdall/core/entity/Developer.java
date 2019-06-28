
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

import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.PrePersist;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a Developer registered to the system.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
@EqualsAndHashCode(of = { "id" })
@RedisHash("developer")
public class Developer implements Serializable {

     private static final long serialVersionUID = -5692191776312048169L;

     @Id
     private String id;

     private String name;

     private String email;

     private String password;

     private LocalDateTime creationDate;

     private List<App> apps;

     private Status status;

     @PrePersist
     private void initValuesPersist() {

          status = (status == null) ? Status.ACTIVE : status;

          creationDate = LocalDateTime.now();
     }
}
