
package br.com.conductor.heimdall.api.repository;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.TypeUser;
import br.com.conductor.heimdall.api.security.CredentialSecurity;

/**
 * Extends {@link JpaRepository}. Provides methods to find a {@link User} and the {@link CredentialSecurity}.
 *
 * @author Marcos Filho
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	 /**
	  * Finds a {@link User} by its username.
	  * 
	  * @param username		The User username.
	  * @return				{@link User}
	  */
     User findByUserName(String username);
     
     /**
      * Finds a {@link User} by its username and {@link TypeUser}.
      * 
	  * @param username		The User username.
      * @param type			{@link TypeUser}
	  * @return				{@link User}
      */
     User findByUserNameAndType(@Param("username") String username, @Param("type") TypeUser type);

     /**
      * Finds a {@link CredentialSecurity} by {@link User} username and {@link TypeUser}.
      * 
	  * @param username		The User username.
      * @param type			{@link TypeUser}
      * @return				{@link CredentialSecurity}
      */
     @Query(value = "SELECT userName as userName, password as password FROM User WHERE userName = ?1 and type = ?2 and status =?3 ")
     CredentialSecurity findCredentialByUserNameAndTypeAndStatus(String username, TypeUser type, Status status);
     
}
