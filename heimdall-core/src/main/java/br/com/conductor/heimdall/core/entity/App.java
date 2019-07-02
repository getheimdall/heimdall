
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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a App registered to the system.
 *
 * @author Filipe Germano
 */
@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@RedisHash("app")
public class App implements Serializable {

    private static final long serialVersionUID = -8080005929936415705L;

    @Id
    private String id;

    private String clientId;

    private String name;

    private String description;

    private Developer developer;

    private LocalDateTime creationDate;

    private Status status;

    private List<AccessToken> accessTokens;

    private List<Plan> plans;


    private void initValuesPersist() {

        status = (status == null) ? Status.ACTIVE : status;

        creationDate = LocalDateTime.now();
        clientId = clientId.trim();
    }


}
