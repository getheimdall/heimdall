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

import br.com.conductor.heimdall.api.dto.UserDTO;
import br.com.conductor.heimdall.api.dto.UserEditDTO;
//import br.com.conductor.heimdall.api.dto.page.UserPage;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.UserType;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.api.repository.UserRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
//import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * Provides methods to create, read, update and delete a {@link User}.
 *
 * @author Marcos Filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CredentialStateService credentialStateService;

    @Autowired
    private RoleService roleService;

    /**
     * Saves a {@link User}.
     *
     * @param user {@link User}
     * @return {@link User}
     */
    @Transactional
    public User save(User user) {

        user.setType(UserType.DATABASE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationDate(LocalDateTime.now());

        user.getRoles().forEach(roleId -> roleService.find(roleId));

        user = userRepository.save(user);

        return user;
    }

    public User findByUsername(String username) {

        User user = userRepository.findByUserName(username);
        HeimdallException.checkThrow(user == null, GLOBAL_NOT_FOUND, "User");

        return user;
    }

    public User findByUsernameAndType(String username, UserType userType) {

        User user = userRepository.findByUserNameAndType(username, userType);
        HeimdallException.checkThrow(user == null, GLOBAL_NOT_FOUND, "User");

        return user;
    }

    /**
     * Finds a {@link User} by its Id.
     *
     * @param id The User Id
     * @return {@link User}
     */
    public User find(String id) {

        User user = userRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(user == null, GLOBAL_NOT_FOUND, "User");
        return user;
    }

    /**
     * Creates a paged list of {@link User} from a request.
     *
     * @param pageable {@link Pageable}
     * @return
     */
    @Transactional
    public Page<User> list(Pageable pageable) {

        return userRepository.findAll(pageable);
    }

    /**
     * Creates a list of {@link User} from a request.
     *
     * @return {@link List} of {@link User}
     */
    @Transactional
    public List<User> list() {

        return userRepository.findAll();
    }

    /**
     * Deletes a {@link User}.
     *
     * @param userId The User Id
     */
    @Transactional
    public void delete(String userId) {

        User user = this.find(userId);

        userRepository.delete(user);
    }

    /**
     * Updates a {@link User}.
     *
     * @param userId  The User Id
     * @param userDTO {@link UserDTO}
     * @return {@link User}
     */
    @Transactional
    public User update(String userId, UserEditDTO userDTO) {

        User user = this.find(userId);

        user = GenericConverter.mapper(userDTO, user);
        user = userRepository.save(user);

        return user;
    }

    /**
     * Updates password the {@link User}
     *
     * @param principal          {@link Principal}
     * @param currentPassword    The current password
     * @param newPassword        The new password
     * @param confirmNewPassword The confirm new password
     */
    public void updatePassword(Principal principal, String currentPassword, String newPassword, String confirmNewPassword) {

        final String username = principal.getName();
        User userLogged = userRepository.findByUserName(username);

        HeimdallException.checkThrow(userLogged == null, GLOBAL_RESOURCE_NOT_FOUND);
        HeimdallException.checkThrow(UserType.LDAP.equals(userLogged.getType()), USER_LDAP_UNAUTHORIZED_TO_CHANGE_PASSWORD);
        HeimdallException.checkThrow(!passwordEncoder.matches(currentPassword, userLogged.getPassword()), USER_CURRENT_PASSWORD_NOT_MATCHING);
        HeimdallException.checkThrow(passwordEncoder.matches(newPassword, userLogged.getPassword()), USER_NEW_PASSWORD_EQUALS_CURRENT_PASSWORD);
        HeimdallException.checkThrow(!newPassword.equals(confirmNewPassword), USER_NEW_PASSWORD_NOT_MATCHING);

        userLogged.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userLogged);
        credentialStateService.revokeByUsername(username);
    }
}
