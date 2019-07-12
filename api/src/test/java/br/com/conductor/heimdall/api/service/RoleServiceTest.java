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

import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.repository.RoleRepository;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

     @InjectMocks
     private RoleService service;

     @Mock
     private RoleRepository roleRepository;

     @Rule
     public ExpectedException thrown = ExpectedException.none();

     @Test
     public void rejectNewRoleWithInvalidPrivileges() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Role already exists");

          Role role = new Role();
          role.setName("name");

          Set<Role> roles = new HashSet<>();
          roles.add(role);

          Mockito.when(roleRepository.findAllByName(Mockito.anyString())).thenReturn(roles);

          service.save(role);
     }

}
