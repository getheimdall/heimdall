
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
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.util.ConstantsCache;

/**
 * <h1>OperationRepository</h1><br/>
 * 
 * Provides methods to access a {@link Operation}.
 *
 * @author Filipe Germano
 *
 */
public interface OperationRepository extends JpaRepository<Operation, Long> {

	 /**
	  * Returns a List of Operation from a endpoint.
	  *  
	  * @param endPoint			- The endpoint that will be searched
	  * @return					The List of Operation's associated with the endpoint
	  */
     @Cacheable(ConstantsCache.OPERATION_ACTIVE_FROM_ENDPOINT)
     @Query("select op from Operation op join fetch op.resource r join fetch r.api a join fetch a.environments e where (a.basePath + op.path) = :endPoint")
     List<Operation> findByEndPoint(@Param("endPoint") String endPoint);
     
     /**
      * Returns a List of Operation associated with a {@link Resource}.
      * 
      * @param idResource		- The Resource Id
      * @return					The List of Operation
      */
     List<Operation> findByResourceId(Long idResource);
     
     /**
      * Finds a Operation by its Id, {@link Api} Id and {@link Resource} Id.
      * 
      * @param apiId			- The Api Id
      * @param resourceId		- The Resource Id
      * @param id				- The Operation Id
      * @return					The Operation found
      */
     Operation findByResourceApiIdAndResourceIdAndId(Long apiId, Long resourceId, Long id);

     /**
      * Returns a List of Operation from a {@link Api} Id and {@link Resource} Id.
      * 
      * @param apiId			- The Api Id
      * @param resourceId		- The Resource Id
      * @return					The List of Operation
      */
     List<Operation> findByResourceApiIdAndResourceId(Long apiId, Long resourceId);

     /**
      * Returns a List of Operation from a {@link Api} Id.
      * 
      * @param apiId			- The Api Id
      * @return					The List of Operation
      */
     List<Operation> findByResourceApiId(Long apiId);
     
     /**
      * Finds a Operation by its path, {@link Resource} Id and HTTP method. 
      * 
      * @param resourceId		- The Resource Id
      * @param method			- The HTTP method
      * @param path				- The path to the Operation
      * @return					The Operation found
      */
     Operation findByResourceIdAndMethodAndPath(Long resourceId, HttpMethod method, String path);

     /**
      * Finds a Operation by its path and HTTP method.
      * 
      * @param method			- The HTTP method
      * @param path				- The path to the Operation
      * @return					The Operation found
      */
     Operation findByMethodAndPath(HttpMethod method, String path);

}
