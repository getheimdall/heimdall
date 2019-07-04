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
package br.com.conductor.heimdall.api.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.UserType;
import org.springframework.stereotype.Repository;

/**
 * Extends {@link JpaRepository}. Provides method to find a {@link Set} of {@link Privilege}.
 *
 * @author Marcos Filho
 * @author <a href="dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, String> {

}
