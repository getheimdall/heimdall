
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

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.util.ConstantsCache;

/**
 * <h1>ApiRepository.java</h1><br/>
 * 
 * Provides methods to access a {@link Api}.
 *
 * @author Filipe Germano
 *
 */
public interface ApiRepository extends JpaRepository<Api, Long> {
     
	/**
	 * Finds a Api by its Status
	 * 
	 * @param status		- The Api {@link Status}
	 * @return				A List of Api
	 */
     List<Api> findByStatus(Status status);
     
     /**
      * Finds a Api by its endpoint.
      * 
      * @param  endPoint	- The endpoint of the Api
      * @return				The Api found
      */
     @Cacheable(ConstantsCache.API_ACTIVE_FROM_ACCESS_TOKEN)
     @Query("select a from Api a join a.resources r join r.operations o where (a.basePath + o.path) = :endPoint")
     Api findByEndPoint(@Param("endPoint") String endPoint);
     
     /**
      * Finds a Api by its basepath.
      * 
      * @param  basePath	- The basepath of the Api
      * @return				The Api found
      */
     Api findByBasePath(String basePath);

}
