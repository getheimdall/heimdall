
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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.util.ConstantsCache;

/**
 * Provides methods to access a {@link Operation}.
 *
 * @author Filipe Germano
 *
 */
public interface OperationRepository extends JpaRepository<Operation, String> {
     
     /**
      * Finds a Operation by its Id, {@link Api} Id and {@link Resource} Id.
      * 
      * @param apiId			The Api Id
      * @param resourceId		The Resource Id
      * @param id				The Operation Id
      * @return					The Operation found
      */
     Operation findByResourceApiIdAndResourceIdAndId(String apiId, String resourceId, String id);

     /**
      * Returns a List of Operation from a {@link Api} Id and {@link Resource} Id.
      * 
      * @param apiId			The Api Id
      * @param resourceId		The Resource Id
      * @return					The List of Operation
      */
     List<Operation> findByResourceApiIdAndResourceId(String apiId, String resourceId);
     
     /**
      * Find an Operation by {@link Api} Id, HTTP method and Operation path.
      * 
      * @param apiId
      * @param method
      * @param path
      * @return
      */
     Operation findByResourceApiIdAndMethodAndPath(String apiId, HttpMethod method, String path);
}
