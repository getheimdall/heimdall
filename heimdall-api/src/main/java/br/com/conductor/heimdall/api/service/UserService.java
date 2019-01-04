
package br.com.conductor.heimdall.api.service;

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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;

import java.util.List;
import java.util.Set;

import br.com.conductor.heimdall.api.dto.UserEditDTO;
import br.com.conductor.heimdall.api.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.api.dto.UserDTO;
import br.com.conductor.heimdall.api.dto.page.UserPage;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.TypeUser;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.api.repository.UserRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * Provides methods to create, read, update and delete a {@link User}.
 *
 * @author Marcos Filho
 *
 */
@Service
public class UserService {

     @Autowired
     private UserRepository userRepository;
     
     @Autowired
     private RoleRepository roleRepository;
     
     @Autowired
     private PasswordEncoder passwordEncoder;
     
     /**
      * Saves a {@link User}.
      * 
      * @param userDTO		{@link UserDTO}
      * @return				{@link User}
      */
     @Transactional
     public User save(UserDTO userDTO) {

          User user = GenericConverter.mapper(userDTO, User.class);
          user.setType(TypeUser.DATABASE);
          user.setPassword(passwordEncoder.encode(user.getPassword()));
          user = userRepository.save(user);
          
          return user;
     }

     public User findByUsername(String username) {
          User userFound = userRepository.findByUserName(username);
          Set<Role> roles = roleRepository.findRolesByUserId(userFound.getId());
          userFound.setRoles(roles);
          return userFound;
     }
     
     /**
      * Finds a {@link User} by its Id.
      * 
      * @param id		The User Id
      * @return			{@link User}
      * @throws NotFoundException
      */
     public User find(Long id) {

          User user = userRepository.findOne(id);          
          HeimdallException.checkThrow(isBlank(user), GLOBAL_RESOURCE_NOT_FOUND);
          
          user.setRoles(roleRepository.findRolesByUserId(id));

          return user;
     }

     /**
      * Creates a paged list of {@link User} from a request.
      * 
      * @param userDTO		{@link UserDTO}
      * @param pageableDTO	{@link PageableDTO}
      * @return				{@link UserPage}
      */
     @Transactional(readOnly = false)
     public UserPage list(UserDTO userDTO, PageableDTO pageableDTO) {

          User user = GenericConverter.mapper(userDTO, User.class);

          Example<User> example = Example.of(user, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<User> page = userRepository.findAll(example, pageable);

          UserPage userPage = new UserPage(PageDTO.build(page));

          return userPage;
     }

     /**
      * Creates a list of {@link User} from a request.
      * 
      * @param userDTO		{@link UserDTO}
      * @return				{@link List} of {@link User}
      */
     @Transactional(readOnly = false)
     public List<User> list(UserDTO userDTO) {

          User user = GenericConverter.mapper(userDTO, User.class);

          Example<User> example = Example.of(user, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          List<User> users = userRepository.findAll(example);

          return users;
     }

     /**
      * Deletes a {@link User}.
      * 
      * @param userId		The User Id
      * @throws NotFoundException
      */
     @Transactional
     public void delete(Long userId) {

          User user = userRepository.findOne(userId);
          HeimdallException.checkThrow(isBlank(user), GLOBAL_RESOURCE_NOT_FOUND);
          
          userRepository.delete(user.getId());
     }

     /**
      * Updates a {@link User}.
      * 
      * @param userId		The User Id
      * @param userDTO		{@link UserDTO}
      * @return				{@link User}
      * @throws NotFoundException
      */
     @Transactional
     public User update(Long userId, UserEditDTO userDTO) {

          User user = userRepository.findOne(userId);
          HeimdallException.checkThrow(isBlank(user), GLOBAL_RESOURCE_NOT_FOUND);
          
          user = GenericConverter.mapper(userDTO, user);
          user = userRepository.save(user);
          
          return user;
     }
}
