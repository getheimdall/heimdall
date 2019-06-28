
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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.enums.Status;

/**
 * Provides methods to access a {@link Middleware}.
 *
 * @author Filipe Germano
 *
 */
public interface MiddlewareRepository extends JpaRepository<Middleware, String> {
     
	 /**
	  * Finds a Middleware by its Id and Status.
	  * 
	  * @param  id			The Middleware Id
	  * @param  status		The Middleware Status
	  * @return				The Middleware found
	  */
     Middleware findByIdAndStatus(String id, Status status);

     /**
      * Finds a List of Middleware by Api Id.
      * 
      * @param  apiId
      * @return				The List of Middleware found
	  */
     List<Middleware> findByApiId(String apiId);

     /**
      * Finds a List of Middleware by Status and Api Id.
      * 
      * @param  status		The Middleware Staus
      * @param  apiId		The Api Id
      * @return				The List of Middleware found
	  */
     List<Middleware> findByStatusAndApiId(Status status, String apiId);
     
     /**
      * Finds a Middleware by its Id and Api Id.
      * 
      * @param  apiId		The Api Id
      * @param  id			The Middleware Id
      * @return				The Middleware found
	  */
     Middleware findByApiIdAndId(String apiId, String id);
     
     /**
      * Finds a Middleware by Api Id and Version.
      * 
      * @param  apiId		The Api Id
      * @param  name		The Middleware Version
      * @return				The Middleware found
	  */
     Middleware findByApiIdAndVersion(String apiId, String name);

	/**
	 * 
	 * @param id interceptor id
	 */
	@Modifying
	@Query(value = "DELETE FROM MIDDLEWARES_INTERCEPTORS WHERE INTERCEPTOR_ID = :ID", nativeQuery = true)
	void detachFromInterceptor(@Param("ID") String id);
          
}
