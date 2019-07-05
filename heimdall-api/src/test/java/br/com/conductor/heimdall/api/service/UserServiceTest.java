package br.com.conductor.heimdall.api.service;

import br.com.conductor.heimdall.api.entity.User;
import br.com.conductor.heimdall.api.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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

          String hashPassword = bcrypt.encode(user.getPassword());

          Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn(hashPassword);

          userService.save(user);

          assertTrue(bcrypt.matches(user.getPassword(), hashPassword));
          assertFalse(bcrypt.matches("654321", hashPassword));
          Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
     }
}
