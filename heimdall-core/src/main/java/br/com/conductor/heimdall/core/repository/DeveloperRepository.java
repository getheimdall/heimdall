
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

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conductor.heimdall.core.entity.Developer;

/**
 * Provides methods to access a {@link Developer}.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

	 /**
	  * Finds a {@link Developer} by its email.
	  * 
	  * @param  email		The Developer email
	  * @return				The Developer found
	  */
     Developer findByEmail(String email);

	/**
	 * Finds a {@link Developer} by its email and password
	 *
	 * @param  email		The Developer email
	 * @param  password		The Developer password
	 * @return				The Developer found
	 */
     Developer findByEmailAndPassword(String email, String password);

}
