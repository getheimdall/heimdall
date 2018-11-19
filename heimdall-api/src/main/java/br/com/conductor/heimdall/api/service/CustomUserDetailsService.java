
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.enums.TypeUser;
import br.com.conductor.heimdall.api.repository.PrivilegeRepository;
import br.com.conductor.heimdall.api.repository.UserRepository;
import br.com.conductor.heimdall.api.security.CredentialSecurity;

/**
 * Implements the {@link UserDetailsService} interface.
 * Loads a specific User data, with its user name, password
 * and Collection of GrantedAuthority.
 *
 * @author Marcos Filho
 *
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

     @Autowired
     private UserRepository repository;
     
     @Autowired
     private PrivilegeRepository privRepository;

     /**
      * Locates a User by its user name. Creates a {@link org.springframework.security.core.userdetails.User} that holds
      * the User's user name, password and Collection of GrantedAuthority.<br>
      * <br>
      * {@link UserDetailsService} documentation: {@inheritDoc}
      */
     @Override
     public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

          final CredentialSecurity credential = repository.findCredentialByUserNameAndTypeAndStatus(username, TypeUser.DATABASE, Status.ACTIVE);
          final Set<Privilege> privileges = privRepository.findPrivilegesByUserNameAndType(username, TypeUser.DATABASE);
          
          return new org.springframework.security.core.userdetails.User(credential.getUserName(), credential.getPassword(), getAuthoritiesFromPrivileges(privileges));
     }
     
     private final Collection<? extends GrantedAuthority> getAuthoritiesFromPrivileges(final Collection<Privilege> privileges) {
          final List<String> auths = new ArrayList<String>();
          
          for (final Privilege item : privileges) {
               auths.add(item.getName());
          }
          
          return getGrantedAuthorities(auths);
     }

     private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {

          final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
          for (final String privilege : privileges) {
               authorities.add(new SimpleGrantedAuthority(privilege));
          }
          return authorities;
     }

}
