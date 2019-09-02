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
package br.com.conductor.heimdall.core.repository;

import br.com.conductor.heimdall.core.entity.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Provides methods to access a {@link Environment}.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {

    /**
     * Finds all Environments by its inbound URL.
     *
     * @param inboundURL The inbound URL
     * @return The Environment found
     */
    List<Environment> findByInboundURL(String inboundURL);

    /**
     * Check if an environment has apis attached
     *
     * @param id
     * @return
     */
    @Query(value = "select count(0) from apis_environments where environment_id = :id", nativeQuery = true)
    Integer findApisWithEnvironment(@Param("id") Long id);

    /**
     * Check if exist any Api with other environment that contain the same inbound_url.
     *
     * @param id      The {@link Environment} id
     * @param inbound Inbound url
     * @return The number of APIs found with the same inbound_url
     */
    @Query(value = "SELECT COUNT(DISTINCT env.id) from environments env INNER JOIN apis_environments api_env ON api_env.environment_id=env.id INNER JOIN apis apis ON apis.id=api_env.api_id INNER JOIN (select apis2.id from apis apis2 INNER JOIN apis_environments api_env2 ON apis2.id=api_env2.api_id INNER JOIN environments env2 ON api_env2.environment_id=env2.id where env2.id = :id) AS a ON (a.id = apis.id) where env.id <> :id and env.inbound_url = :inbound", nativeQuery = true)
    Integer findApiWithOtherEnvironmentEqualsInbound(@Param("id") Long id, @Param("inbound") String inbound);

}
