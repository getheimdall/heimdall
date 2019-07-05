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

import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.UserType;
import br.com.conductor.heimdall.api.repository.PrivilegeRepository;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_NOT_FOUND;

/**
 * Provides methods to find one or mode {@link Privilege}.
 *
 * @author Marcos Filho
 */
@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * Finds a {@link Privilege} by its Id.
     *
     * @param id The Privilege Id
     * @return {@link Privilege}
     */
    public Privilege find(String id) {

        Privilege privilege = privilegeRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(privilege == null, GLOBAL_NOT_FOUND, "Privilege");

        return privilege;
    }

    /**
     * Finds all {@link Privilege} from a paged request.
     *
     * @param pageable {@link Pageable}
     * @return
     */
    public Page<Privilege> list(Pageable pageable) {

        return privilegeRepository.findAll(pageable);
    }

    /**
     * Finds a {@link List} of {@link Privilege} associated with one Privilege provided.
     *
     * @return {@link List} of {@link Privilege}
     */
    public List<Privilege> list() {

        return privilegeRepository.findAll();
    }

    public Set<Privilege> list(String username) {

        final User user = userService.findByUsername(username);

        return list(user);
    }

    public Set<Privilege> list(String username, UserType userType) {

        final User user = userService.findByUsernameAndType(username, userType);

        return list(user);
    }

    public Set<Privilege> list(User user) {
        final Set<Privilege> privileges = new HashSet<>();

        user.getRoles().stream().map(roleId -> roleService.find(roleId)).collect(Collectors.toSet())
                .forEach(role -> role.getPrivileges().stream()
                        .map(privilegeId -> privilegeRepository.findById(privilegeId).orElse(null))
                        .filter(Objects::nonNull)
                        .forEach(privileges::add));

        return privileges;
    }

    public Privilege save(Privilege privilege) {

        return privilegeRepository.save(privilege);
    }
}
