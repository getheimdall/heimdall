package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.api.service.TokenAuthenticationService;
import br.com.conductor.heimdall.core.dto.persist.AccessTokenPersist;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.service.AccessTokenService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.RabbitQueueUtils;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@ContextConfiguration
@WebAppConfiguration
@AutoConfigureMockMvc
public class AccessTokenResourceTest {

     private MockMvc mockMvc;
     
     @MockBean
     private AccessTokenService service;

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

         Authentication authentication =
                 new UsernamePasswordAuthenticationToken("tester", "password",
                         Collections.singletonList(new SimpleGrantedAuthority("CREATE_ACCESSTOKEN")));

         Mockito.when(tokenAuthenticationService.getAuthentication(Mockito.any(), Mockito.any())).thenReturn(authentication);
         mockMvc = MockMvcBuilders.webAppContextSetup(context)
                 .apply(SecurityMockMvcConfigurers.springSecurity()).addFilter(filterChain).build();
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithoutRequiredField() throws Exception {
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ACCESS_TOKENS, 10L)
                 .content("{\"code\":\"!!@!##1212\"}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                 .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"erros\":[{\"defaultMessage\":\"may not be null\",\"field\":\"app\",\"code\":\"NotNull\"}]}"));
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithoutDefaultValues() throws Exception {
          
          AccessToken recoverAt = new AccessToken();
          recoverAt.setId(10L);
          
          Mockito.when(service.save(Mockito.any(AccessTokenPersist.class))).thenReturn(recoverAt);
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ACCESS_TOKENS, 10L)
                 .content("{\"code\":\"!!@!##1212\",\"app\":{\"id\":10}}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isCreated());
     }
}
