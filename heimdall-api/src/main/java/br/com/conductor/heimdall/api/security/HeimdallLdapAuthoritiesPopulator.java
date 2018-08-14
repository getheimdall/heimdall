
package br.com.conductor.heimdall.api.security;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.enums.TypeUser;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.api.repository.UserRepository;

import javax.transaction.Transactional;

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

     /**
      * If it can not find the user it creates one from the {@link DirContextOperations} provided.<br>
      * <br>
      * {@inheritDoc}
      */
     @Transactional
     @Override
     public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {

          User user = repository.findByUserNameAndType(username, TypeUser.LDAP);

          if (user == null) {
               User addUser = new User();
               addUser.setEmail(userData.getStringAttribute("mail"));
               addUser.setFirstName(userData.getStringAttribute("givenName"));
               addUser.setLastName(userData.getStringAttribute("sn"));
               addUser.setType(TypeUser.LDAP);
               addUser.setPassword(UUID.randomUUID().toString());
               addUser.setUserName(username);
               
               Set<Role> roles = roleRepository.findByName(Role.DEFAULT);
               addUser.setRoles(roles);
               
               repository.save(addUser);
               user = addUser;
          }

          return getAuthorities(user.getRoles());
     }

     /*
      * Returns a Collection of granted authorities.
      */
     private final Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {

          return getGrantedAuthorities(getPrivileges(roles));
     }

     /*
      * Returns a list of privileges.
      */
     private final List<String> getPrivileges(final Collection<Role> roles) {

          final List<String> privileges = new ArrayList<String>();
          final Set<Privilege> collection = new HashSet<Privilege>();
          for (final Role role : roles) {
               collection.addAll(role.getPrivileges());
          }
          for (final Privilege item : collection) {
               privileges.add(item.getName());
          }

          return privileges;
     }

     /*
      * Returns a List of granted authorities.
      */
     private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {

          final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
          for (final String privilege : privileges) {
               authorities.add(new SimpleGrantedAuthority(privilege));
          }
          return authorities;
     }

}
