package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.service.TokenAuthenticationService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.api.service.RoleService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.RabbitQueueUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@AutoConfigureMockMvc
public class UserResourceTest {

     @Autowired
     private MockMvc mockMvc;
     
     @MockBean
     private RoleService service;

     @MockBean
     private TokenAuthenticationService tokenAuthenticationService;

     @Autowired
     private WebApplicationContext context;

     @Autowired
     private FilterChainProxy filterChain;
     
     @BeforeClass
     public static void setup() {
          RabbitQueueUtils.init();
     }

     @Before
     public void setupTest() {
          SimpleGrantedAuthority readUser = new SimpleGrantedAuthority("READ_USER");
          SimpleGrantedAuthority createUser = new SimpleGrantedAuthority("CREATE_USER");
          SimpleGrantedAuthority deleteUser = new SimpleGrantedAuthority("DELETE_USER");
          SimpleGrantedAuthority updateUser = new SimpleGrantedAuthority("UPDATE_USER");
          Authentication authentication =
                  new UsernamePasswordAuthenticationToken("tester", "password",
                          Arrays.asList(readUser, createUser, deleteUser, updateUser));

          Mockito.when(tokenAuthenticationService.getAuthentication(Mockito.any(), Mockito.any())).thenReturn(authentication);
          mockMvc = MockMvcBuilders.webAppContextSetup(context)
                  .apply(SecurityMockMvcConfigurers.springSecurity()).addFilter(filterChain).build();
     }

     @Test
     @WithMockUser(username="tester", authorities={"READ_USER", "CREATE_USER", "DELETE_USER", "UPDATE_USER"})
     public void deniedToAddUserWithoutRequiredFields() throws Exception {
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_USERS)
                    .content("{\"email\":\"foobar@foobar.com.br\",\"firstName\":\"foo\",\"lastName\":\"bar\",\"username\":\"\",\"password\":\"123456\"}")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError());
     }
     
}
