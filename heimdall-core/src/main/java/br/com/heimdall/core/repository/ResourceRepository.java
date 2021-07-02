
package br.com.heimdall.core.repository;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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

import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provide methods to find a specific {@link Resource} or a List of them.
 *
 * @author Filipe Germano
 *
 */
public interface ResourceRepository extends JpaRepository<Resource, Long> {
     
	 /**
	  * Finds a Resource by its Id and {@link Api} Id.
	  *  
	  * @param apiId		The Api Id
	  * @param id			The Resource Id
	  * @return				The Resource found
	  */
     Resource findByApiIdAndId(Long apiId, Long id);
     
     /**
      * Finds a list of Resource's for a {@link Api} by the Api's Id.
      * 
	  * @param apiId		The Api Id
      * @return				The List of Resource's found
      */
     List<Resource> findByApiId(Long apiId);

     /**
      * Finds a Resource by its name and {@link Api} Id.
      * 
	  * @param apiId		The Api Id
      * @param name			The Resource name
	  * @return				The Resource found
      */
     Resource findByApiIdAndName(Long apiId, String name);
     
}
