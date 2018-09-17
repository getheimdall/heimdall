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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.api.dto.RoleDTO;
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.service.RoleService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.RabbitQueueUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@ContextConfiguration
@WebAppConfiguration
@AutoConfigureMockMvc
public class RoleResourceTest {

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
          SimpleGrantedAuthority readRole = new SimpleGrantedAuthority("READ_ROLE");
          SimpleGrantedAuthority createRole = new SimpleGrantedAuthority("CREATE_ROLE");
          SimpleGrantedAuthority deleteRole = new SimpleGrantedAuthority("DELETE_ROLE");
          SimpleGrantedAuthority updateRole = new SimpleGrantedAuthority("UPDATE_ROLE");
          Authentication authentication =
                  new UsernamePasswordAuthenticationToken("tester", "password",
                          Arrays.asList(readRole, createRole, deleteRole, updateRole));

          Mockito.when(tokenAuthenticationService.getAuthentication(Mockito.any(), Mockito.any())).thenReturn(authentication);
          mockMvc = MockMvcBuilders.webAppContextSetup(context)
                  .apply(SecurityMockMvcConfigurers.springSecurity()).addFilter(filterChain).build();
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"READ_ROLE", "CREATE_ROLE", "DELETE_ROLE", "UPDATE_ROLE"})
     public void getAllRoles() throws Exception {
          mockMvc.perform(MockMvcRequestBuilders.get(ConstantsPath.PATH_ROLES)
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"READ_ROLE", "CREATE_ROLE", "DELETE_ROLE", "UPDATE_ROLE"})
     public void notPermitToPersistRoleWithPrivilegesMissing() throws Exception {
          Role role = new Role();
          role.setId(100L);
          Mockito.when(service.save(Mockito.any(RoleDTO.class))).thenReturn(role);
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ROLES)
                    .content("{\"name\":\"\",\"privileges\":[{\"id\":5},{\"id\":2},{\"id\":10}]}")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                    .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"erros\":[{\"defaultMessage\":\"size must be between 6 and 80\",\"objectName\":\"roleDTO\",\"field\":\"name\",\"code\":\"Size\"}]}"));
     }
}
