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
package br.com.conductor.heimdall.api.entity;

import br.com.conductor.heimdall.api.enums.UserType;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data class that represents the User.
 *
 * @author Marcos Filho
 */
@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("user")
public class User implements Serializable {

    private static final long serialVersionUID = -7740868543851971847L;

    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Indexed
    private String userName;

    private String email;

    private String password;

    @Indexed
    private Status status;

    private LocalDateTime creationDate;

    @Indexed
    private UserType type;

    private Set<String> roles;

}
