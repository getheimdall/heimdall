/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.api.service;

import br.com.conductor.heimdall.api.dto.RoleDTO;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_NOT_FOUND;

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
    private PrivilegeService privilegeService;

    /**
     * Saves a {@link Role}.
     *
     * @param role {@link Role}
     * @return                {@link Role} saved
     */
    @Transactional
    public Role save(Role role) {

        Set<Role> nameRole = roleRepository.findByName(role.getName());

        HeimdallException.checkThrow(nameRole.size() > 0, ExceptionMessage.ROLE_ALREADY_EXIST);

        role.getPrivileges().forEach(p -> privilegeService.find(p));

        role = roleRepository.save(role);

        return role;
    }

    /**
     * Finds a {@link Role} by its Id.
     *
     * @param id The Role Id
     * @return                {@link Role}
     */
    public Role find(String id) {

        Role role = roleRepository.findOne(id);
        HeimdallException.checkThrow(role == null, GLOBAL_NOT_FOUND, "Role");

        return role;
    }

    /**
     * Creates a paged list of {@link Role} from a request.
     *
     * @param pageable {@link Pageable}
     * @return
     */
    @Transactional
    public Page<Role> list(Pageable pageable) {

        return roleRepository.findAll(pageable);
    }

    /**
     * Creates a list of {@link Role} from a request.
     *
     * @return                {@link List} of {@link Role}
     */
    @Transactional
    public List<Role> list() {

        return roleRepository.findAll();
    }

    /**
     * Deletes a {@link Role}.
     *
     * @param roleId The Role Id
     */
    @Transactional
    public void delete(String roleId) {

        Role role = this.find(roleId);

        roleRepository.delete(role);
    }

    /**
     * Updates a {@link Role}.
     *
     * @param roleId      The Role Id
     * @param rolePersist {@link RoleDTO}
     * @return                {@link Role}
     */
    @Transactional
    public Role update(String roleId, Role rolePersist) {

        Role role = this.find(roleId);
        Set<Role> roleByName = roleRepository.findByName(rolePersist.getName());

        if (roleByName.size() > 0) {
            HeimdallException.checkThrow(roleByName.stream().anyMatch(r -> !r.getId().equals(roleId) && r.getName().equals(rolePersist.getName())), ExceptionMessage.ROLE_ALREADY_EXIST);
        }

        Role roleMapper = GenericConverter.mapper(rolePersist, role);
        return roleRepository.save(roleMapper);
    }
}
