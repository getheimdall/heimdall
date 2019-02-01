
package br.com.conductor.heimdall.core.repository;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.util.ConstantsCache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * Provides methods to access a {@link App}.
 *
 * @author Filipe Germano
 *
 */
public interface AppRepository extends JpaRepository<App, Long> {

     /**
	 * Finds a active App by its client Id.
	 * 
	 * @param  clientId		The client id
	 * @return				The App found
	 */
     @Lock(LockModeType.NONE)
     @Cacheable(ConstantsCache.APPS_ACTIVE_CACHE)
     @Query("select a from App a join a.plans p where a.clientId = :clientId and a.status = 'ACTIVE' and p.status = 'ACTIVE' ")
     App findAppActive(@Param("clientId") String clientId);

     /**
	 * Finds a App by its client Id.
	 * 
	 * @param  clientId		The client id
	 * @return				The App found
	 */
     @Lock(LockModeType.NONE)
     @Cacheable(ConstantsCache.APPS_CLIENT_ID)
	App findByClientId(String clientId);

	/**
	 * Finds a App by its name.
	 * 
	 * @param  name			The App name
	 * @return				The App found
	 */
     @Lock(LockModeType.NONE)
	App findByName(String name);

	/**
	 * Finds a List of {@link Plan} associated with a App.
	 * 
	 * @param  appId		The App Id
	 * @return				The list of Plan
	 */
     @Lock(LockModeType.NONE)
	@Query("select p from App a join a.plans p where a.id = :appId")
	List<Plan> findPlansByApp(@Param("appId") Long appId);

}
