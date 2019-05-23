/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
package br.com.conductor.heimdall.api.repository;

import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LDAP Repository.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public interface LdapRepository extends JpaRepository<Ldap, Long> {

    Ldap findByStatus(Status status);
}
