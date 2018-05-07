
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

import br.com.conductor.heimdall.core.entity.RateLimit;

/**
 * Provides methods to save, find and delete a {@link RateLimit}.
 *
 * @author Marcos Filho
 *
 */
public interface RateLimitRepository {

	 /**
	  * Saves a {@link RateLimit} to the repository and returns the RateLimit saved.
	  * 
	  * @param rate			The RateLimit to be saved
	  * @return				The saved RateLimit
	  */
     RateLimit save(RateLimit rate);

     /**
      * Finds a RateLimit on the repository.
      * 
      * @param path			The path to the RateLimit
      * @return				The RateLimit found
      */
     RateLimit find(String path);

     /**
      * Deletes a RateLimit from the repository.
      * 
      * @param path			The path to the RateLimit
      */
     void delete(String path);

}
