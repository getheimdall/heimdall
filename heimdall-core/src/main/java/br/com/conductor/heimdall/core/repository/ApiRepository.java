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

import br.com.conductor.heimdall.core.entity.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Provides methods to access a {@link Api}.
 *
 * @author Filipe Germano
 *
 */
public interface ApiRepository extends JpaRepository<Api, String> {
     
     /**
      * Finds a Api by its basepath.
      * 
      * @param  basePath	The basepath of the Api
      * @return				The Api found
      */
     Api findByBasePath(String basePath);

}
