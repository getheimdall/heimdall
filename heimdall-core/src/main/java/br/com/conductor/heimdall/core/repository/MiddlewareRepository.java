
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

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.enums.Status;

/**
 * <h1>MiddlewareRepository</h1><br/>
 * 
 * Provides methods to access a {@link Middleware}.
 *
 * @author Filipe Germano
 *
 */
public interface MiddlewareRepository extends JpaRepository<Middleware, Long> {
     
	 /**
	  * Finds a Middleware by its Id and Status.
	  * 
	  * @param  id			- The Middleware Id
	  * @param  status		- The Middleware Status
	  * @return				The Middleware found
	  */
     Middleware findByIdAndStatus(Long id, Status status);

     /**
      * Finds a List of Middleware by Status.
      * 
      * @param  status
      * @return				The List of Middleware found
	  */
     List<Middleware> findByStatus(Status status);

     /**
      * Finds a List of Middleware by Api Id.
      * 
      * @param  apiId
      * @return				The List of Middleware found
	  */
     List<Middleware> findByApiId(Long apiId);

     /**
      * Finds a List of Middleware by Status and Api Id.
      * 
      * @param  status		- The Middleware Staus
      * @param  apiId		- The Api Id
      * @return				The List of Middleware found
	  */
     List<Middleware> findByStatusAndApiId(Status status, Long apiId);
     
     /**
      * Finds a Middleware by its Id and Api Id.
      * 
      * @param  apiId		- The Api Id
      * @param  id			- The Middleware Id
      * @return				The Middleware found
	  */
     Middleware findByApiIdAndId(Long apiId, Long id);
     
     /**
      * Finds a Middleware by Api Id and Version.
      * 
      * @param  apiId		- The Api Id
      * @param  name		- The Middleware Version
      * @return				The Middleware found
	  */
     Middleware findByApiIdAndVersion(Long apiId, String name);
     
     /**
      * Finds a the first Middleware by Api Id.
      * 
      * @param  apiId		- The Api Id
      * @return				The Middleware found
	  */
     Middleware findTop1ByApiIdOrderByVersionDesc(Long apiId);
          
}
