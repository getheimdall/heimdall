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

import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
          User user = new User();
          user.setEmail("foobar@email.com.br");
          user.setUserName("foobar");
          user.setFirstName("foo");
          user.setLastName("bar");
          user.setPassword("123456");
          user.setRoles(new HashSet<>());

          String hashPassword = bcrypt.encode(user.getPassword());

          Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(hashPassword);
          Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

          User savedUser = userService.save(user);

          assertTrue(bcrypt.matches("123456", savedUser.getPassword()));
          assertFalse(bcrypt.matches("654321", savedUser.getPassword()));
          Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
     }
}
