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
package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.api.service.TokenAuthenticationService;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.service.AccessTokenService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import org.junit.AfterClass;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import redis.embedded.RedisServerBuilder;

import java.io.IOException;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@ContextConfiguration
@WebAppConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
     public void testSavingAccessTokenWithoutApp() throws Exception {

          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ACCESS_TOKENS, 10L)
                                                .content("{\"code\":\"!!@!##1212\", \"plans\": [\"abc123\"]}")
                                                .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                 .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"errors\":[{\"defaultMessage\":\"must not be null\",\"objectName\":\"accessTokenPersist\",\"field\":\"app\",\"reason\":\"NotNull\"}]}"));
     }

     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithEmptyPlans() throws Exception {

          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ACCESS_TOKENS, 10L)
                                                .content("{\"code\":\"!!@!##1212\",\"app\":\"10L\",\"plans\":[]}")
                                                .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                 .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"errors\":[{\"defaultMessage\":\"size must be between 1 and 2147483647\",\"objectName\":\"accessTokenPersist\",\"field\":\"plans\",\"reason\":\"Size\"}]}"));
     }

     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithoutPlans() throws Exception {

          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ACCESS_TOKENS, 10L)
                                                .content("{\"code\":\"!!@!##1212\",\"app\":\"10\"}")
                                                .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                 .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"errors\":[{\"defaultMessage\":\"must not be null\",\"objectName\":\"accessTokenPersist\",\"field\":\"plans\",\"reason\":\"NotNull\"}]}"));
     }

     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithoutDefaultValues() throws Exception {

          AccessToken recoverAt = new AccessToken();
          recoverAt.setId("10L");

          Mockito.when(service.save(Mockito.any(AccessToken.class))).thenReturn(recoverAt);

          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ACCESS_TOKENS, 10L)
                 .content("{\"code\":\"!!@!##1212\",\"app\":\"10\",\"plans\":[\"20\"]}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isCreated());
     }
}
