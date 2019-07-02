
package br.com.conductor.heimdall.core.repository;

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

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.util.ConstantsCache;

/**
 * Provides methods to access a {@link AccessToken}.
 *
 * @author Filipe Germano
 */
public interface AccessTokenRepository extends JpaRepository<AccessToken, String> {

    /**
     * Finds a active {@link AccessToken} by its code.
     *
     * @param code The AccessToken code
     * @return The AccessToken
     */
    @Query("select ac from AccessToken ac join ac.plans p join fetch ac.app a where ac.code = :code and ac.status = 'ACTIVE' and (ac.expiredDate >= CURRENT_TIMESTAMP or ac.expiredDate is null) and p.status = 'ACTIVE' ")
    AccessToken findAccessTokenActive(@Param("code") String code);

    /**
     * Finds a {@link AccessToken} by its code.
     *
     * @param code The AccessToken code
     * @return The AccessToken
     */
    AccessToken findByCode(String code);

    /**
     * Finds a {@link AccessToken} by its Id.
     *
     * @param id The AccessToken id
     * @return The AccessToken
     */
    List<AccessToken> findByApp(String id);

}
