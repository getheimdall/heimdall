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
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a Scope registered to the system.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@EqualsAndHashCode(of = { "id" })
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("scope")
public class Scope implements Serializable {

	private static final long serialVersionUID = 7495733828659838366L;

	@Id
    private String id;

	@Indexed
    private String name;

    private String description;

    @Indexed
    private String api;

    private Set<String> operations;

}
