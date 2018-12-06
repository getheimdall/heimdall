
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

import br.com.conductor.heimdall.api.dto.RoleDTO;
import br.com.conductor.heimdall.api.dto.page.RolePage;
import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.repository.PrivilegeRepository;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.Pageable;
import br.com.twsoftware.alfred.object.Objeto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.PRIVILEGES_NOT_EXIST;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;

/**
 * Provides methods to create, read, update and delete a {@link Role}.
 *
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class RoleService {

     @Autowired
     private RoleRepository roleRepository;
     
     @Autowired
     private PrivilegeRepository privilegeRepository;
     
     /**
      * Saves a {@link Role}.
      *      
      * @param roleDTO		{@link RoleDTO}
      * @return				{@link Role} saved
      * @throws				BadRequestException
      */
     @Transactional
     public Role save(RoleDTO roleDTO) {

          Role role = GenericConverter.mapper(roleDTO, Role.class);

          Set<Role> nameRole = roleRepository.findByName(roleDTO.getName());

          HeimdallException.checkThrow(nameRole.size() > 0, ExceptionMessage.ROLE_ALREADY_EXIST);

          List<Long> invalidPrivileges = new ArrayList<>();
          role.getPrivileges().forEach(p -> {
               Privilege privilege = privilegeRepository.findOne(p.getId());
               if (Objeto.isBlank(privilege)) {
                    invalidPrivileges.add(p.getId());
               }
          });
          
          HeimdallException.checkThrow(!invalidPrivileges.isEmpty(), PRIVILEGES_NOT_EXIST, invalidPrivileges.toString());
          
          role = roleRepository.save(role);
          
          return role;
     }

     /**
      * Finds a {@link Role} by its Id.
      * 
      * @param roleId		The Role Id
      * @return				{@link Role}
      * @throws				NotFoundException
      */
     public Role find(Long id) {

          Role role = roleRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(role), GLOBAL_RESOURCE_NOT_FOUND);

          return role;
     }

     /**
      * Creates a paged list of {@link Role} from a request.
      * 
      * @param roleDTO		{@link RoleDTO}
      * @param pageableDTO	{@link PageableDTO}
      * @return				{@link RolePage}
      */
     @Transactional(readOnly = false)
     public RolePage list(RoleDTO roleDTO, PageableDTO pageableDTO) {

          Role role = GenericConverter.mapper(roleDTO, Role.class);

          Example<Role> example = Example.of(role, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<Role> page = roleRepository.findAll(example, pageable);

          RolePage rolePage = new RolePage(PageDTO.build(page));

          return rolePage;
     }

     /**
      * Creates a list of {@link Role} from a request.
      * 
      * @param roleDTO		{@link RoleDTO}
      * @return				{@link List} of {@link Role}
      */
     @Transactional(readOnly = false)
     public List<Role> list(RoleDTO roleDTO) {

          Role role = GenericConverter.mapper(roleDTO, Role.class);

          Example<Role> example = Example.of(role, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));

          List<Role> roles = roleRepository.findAll(example);

          return roles;
     }

     /**
      * Deletes a {@link Role}.
      * 
      * @param roleId		The Role Id
      * @throws				NotFoundException
      */
     @Transactional
     public void delete(Long roleId) {

          Role role = roleRepository.findOne(roleId);
          HeimdallException.checkThrow(isBlank(role), GLOBAL_RESOURCE_NOT_FOUND);
          
          roleRepository.delete(role.getId());
     }

     /**
      * Updates a {@link Role}.
      * 
      * @param roleId		The Role Id
      * @param roleDTO		{@link RoleDTO}
      * @return				{@link Role}
      * @throws				NotFoundException
      */
     @Transactional
     public Role update(Long roleId, RoleDTO roleDTO) {

          Role role = roleRepository.findOne(roleId);
          HeimdallException.checkThrow(isBlank(role), GLOBAL_RESOURCE_NOT_FOUND);
          Set<Role> roleByName = roleRepository.findByName(roleDTO.getName());

          if (roleByName.size() > 0){
               HeimdallException.checkThrow(roleByName.stream().anyMatch(r -> !r.getId().equals(roleId) && r.getName().equals(roleDTO.getName())), ExceptionMessage.ROLE_ALREADY_EXIST);
          }

          Role roleMapper = GenericConverter.mapper(roleDTO, role);
          return roleRepository.save(roleMapper);
     }
}
