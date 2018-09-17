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
package br.com.conductor.heimdall.api.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.conductor.heimdall.api.dto.UserDTO;
import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
     
     @InjectMocks
     private UserService userService;
     
     @Mock
     private UserRepository userRepository;
     
     @Mock
     private PasswordEncoder passwordEncoder;
     
     private BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(11);

     @Test
     public void avoidPlainTextPassword() {
          UserDTO userDTO = new UserDTO();
          userDTO.setEmail("foobar@email.com.br");
          userDTO.setUserName("foobar");
          userDTO.setFirstName("foo");
          userDTO.setLastName("bar");
          userDTO.setPassword("123456");
          
          String hashPassword = bcrypt.encode(userDTO.getPassword());
          
          Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(hashPassword);
          
          userService.save(userDTO);
          
          assertTrue(bcrypt.matches(userDTO.getPassword(), hashPassword));
          assertFalse(bcrypt.matches("654321", hashPassword));
          Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
     }
}
