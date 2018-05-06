package br.com.conductor.heimdall.api.integration;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import br.com.conductor.heimdall.api.ApiApplication;
import br.com.conductor.heimdall.core.dto.DeveloperDTO;
import br.com.conductor.heimdall.core.dto.integration.AccessTokenDTO;
import br.com.conductor.heimdall.core.dto.integration.AppCallbackDTO;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.service.AccessTokenService;
import br.com.conductor.heimdall.core.service.AppService;
import br.com.conductor.heimdall.core.service.DeveloperService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.RabbitQueueUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=ApiApplication.class)
@AutoConfigureMockMvc
public class IntegrationResourceTest {

     @Autowired
     private MockMvc mockMvc;
     
     @MockBean
     private AccessTokenService tokenService;
     
     @MockBean
     private DeveloperService developerService;
     
     @MockBean
     private AppService appService;
     
     @BeforeClass
     public static void setup() {
          RabbitQueueUtils.init();
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithSpecialCharacters() throws Exception {         
          Mockito.when(tokenService.save(Mockito.any(AccessTokenDTO.class))).thenReturn(new AccessToken());
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_INTEGRATION_RESOURCES+"/access-token/callback")
                 .content("{\"code\":\"!!@@@#3333%\",\"app\":{\"code\":\"525252\"},\"status\":\"ACTIVE\"}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAccessTokenWithoutRequiredField() throws Exception {         
          Mockito.when(tokenService.save(Mockito.any(AccessTokenDTO.class))).thenReturn(new AccessToken());
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_INTEGRATION_RESOURCES+"/access-token/callback")
                 .content("{\"app\":{\"code\":\"525252\"},\"status\":\"ACTIVE\"}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().is4xxClientError());
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingDeveloperWithoutRequiredField() throws Exception {          
          Mockito.when(developerService.save(Mockito.any(DeveloperDTO.class))).thenReturn(new Developer());
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_INTEGRATION_RESOURCES+"/developer/callback")
                 .content("{\"name\":\"NoNamebypass\",\"status\":\"ACTIVE\"}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                 .andExpect(MockMvcResultMatchers.content().json("{\"status\":400,\"exception\":\"MethodArgumentNotValidException\",\"erros\":[{\"defaultMessage\":\"may not be null\",\"field\":\"email\",\"code\":\"NotNull\"}]}"));
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingDeveloperWithDefaultValues() throws Exception {        
          Mockito.when(developerService.save(Mockito.any(DeveloperDTO.class))).thenReturn(new Developer());
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_INTEGRATION_RESOURCES+"/developer/callback")
                 .content("{\"name\":\"markmark\",\"email\":\"default@mail.com.br\"}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
     
     @Test
     @WithMockUser(username="tester", authorities={"CREATE_ACCESSTOKEN"})
     public void testSavingAppWithDefaultValues() throws Exception {        
          Mockito.when(appService.save(Mockito.any(AppCallbackDTO.class))).thenReturn(new App());
          
          mockMvc.perform(MockMvcRequestBuilders.post(ConstantsPath.PATH_INTEGRATION_RESOURCES+"/app/callback")
                 .content("{\"name\":\"mark mark\",\"developer\":\"default@mail.com.br\",\"code\":\"!!!3254@#@!$\"}")
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(MockMvcResultMatchers.status().isOk());
     }
     
     
}
