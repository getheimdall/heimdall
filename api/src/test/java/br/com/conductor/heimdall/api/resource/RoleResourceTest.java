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
import br.com.conductor.heimdall.api.entity.Role;
import br.com.conductor.heimdall.api.service.RoleService;
import br.com.conductor.heimdall.api.service.TokenAuthenticationService;
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
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ApiApplication.class)
@ContextConfiguration
@WebAppConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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

    private static RedisServer redisServer;

    @BeforeClass
    public static void setUp() throws IOException {
        redisServer = new RedisServer(getTemporaryPort());
        redisServer.start();

    }

    @AfterClass
    public static void destroy() {
        redisServer.stop();
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
    @WithMockUser(username = "tester", authorities = {"READ_ROLE", "CREATE_ROLE", "DELETE_ROLE", "UPDATE_ROLE"})
    public void getAllRoles() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(ConstantsPath.PATH_ROLES)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "tester", authorities = {"READ_ROLE", "CREATE_ROLE", "DELETE_ROLE", "UPDATE_ROLE"})
    public void notPermitToPersistRoleWithPrivilegesMissing() throws Exception {
        Role role = new Role();
        role.setId("100L");
        Mockito.when(service.save(Mockito.any(Role.class))).thenReturn(role);

        mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_ROLES)
                .content("{\"name\":\"\",\"privileges\":[\"5\",\"2\",\"10\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"errors\":[{\"defaultMessage\":\"size must be between 6 and 80\",\"objectName\":\"roleDTO\",\"field\":\"name\",\"reason\":\"Size\"}]}"));
    }

    private static int getTemporaryPort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

}
