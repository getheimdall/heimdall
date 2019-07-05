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
package br.com.conductor.heimdall.api.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.conductor.heimdall.api.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.UserType;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.api.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implements the {@link LdapAuthoritiesPopulator}. Provides a method to get the granted authorities.
 *
 * @author Marcos Filho
 *
 */
@Component
public class HeimdallLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

     @Autowired
     private UserRepository repository;
     
     @Autowired
     private RoleRepository roleRepository;

     @Autowired
     private PrivilegeService privilegeService;

     /**
      * If it can not find the user it creates one from the {@link DirContextOperations} provided.<br>
      * <br>
      * {@inheritDoc}
      */
     @Transactional
     @Override
     public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {

          User user = repository.findByUserNameAndType(username, UserType.LDAP);

          if (user == null) {
               User addUser = new User();
               addUser.setEmail(userData.getStringAttribute("mail"));
               addUser.setFirstName(userData.getStringAttribute("givenName"));
               addUser.setLastName(userData.getStringAttribute("sn"));
               addUser.setType(UserType.LDAP);
               addUser.setPassword(UUID.randomUUID().toString());
               addUser.setUserName(username);
               
               Set<Role> roles = roleRepository.findAllByName(Role.DEFAULT);
               addUser.setRoles(roles.stream().map(Role::getId).collect(Collectors.toSet()));
               
               repository.save(addUser);
               user = addUser;
          }

          return getAuthorities(user);
     }

     /*
      * Returns a Collection of granted authorities.
      */
     private final Collection<? extends GrantedAuthority> getAuthorities(final User user) {

          return getGrantedAuthorities(privilegeService.list(user).stream().map(Privilege::getId).collect(Collectors.toSet()));
     }

     /*
      * Returns a List of granted authorities.
      */
     private final List<GrantedAuthority> getGrantedAuthorities(final Set<String> privileges) {

          final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
          for (final String privilege : privileges) {
               authorities.add(new SimpleGrantedAuthority(privilege));
          }
          return authorities;
     }

}
