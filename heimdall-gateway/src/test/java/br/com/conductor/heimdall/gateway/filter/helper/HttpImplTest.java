package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class HttpImplTest {
     
     @InjectMocks
     private HttpImpl subject;
     
     @Mock
     private RestTemplate restTemplate;
     
     private ResponseEntity<String> responseEntity;
     
     @Before
     public void setup() {
          responseEntity = ResponseEntity.ok("OK");
     }
     
     @Test
     public void sendGetWithNoParams() {
          Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.any(Class.class))).thenReturn(responseEntity);
          
          ApiResponse apiResponse = subject.url("https://www.google.com/search")
                                           .sendGet();
          assertNotNull(apiResponse);
          assertNotNull(apiResponse.getStatus());
          assertNotNull(apiResponse.getBody());
          assertEquals(apiResponse.getStatus().intValue(), HttpStatus.OK.value());
          assertEquals(apiResponse.getBody(), "OK");
     }
     
     @Test
     public void sendGetWithInvalidParams() {
          Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.any(Class.class))).thenReturn(responseEntity);
          
          ApiResponse apiResponse = subject.url("https://www.google.com/search")
                                           .queryParam("search", null)
                                           .sendGet();
          assertNotNull(apiResponse);
          assertNotNull(apiResponse.getStatus());
          assertNotNull(apiResponse.getBody());
          assertEquals(apiResponse.getStatus().intValue(), HttpStatus.OK.value());
          assertEquals(apiResponse.getBody(), "OK");
     }
     
     @Test
     public void setUrlThenAddQueryParam() {
          Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.any(Class.class))).thenReturn(responseEntity);
          
          ApiResponse apiResponse = subject.url("https://www.google.com/search")
                                           .queryParam("search", "Heimdall")
                                           .sendGet();
          assertNotNull(apiResponse);
          assertNotNull(apiResponse.getStatus());
          assertNotNull(apiResponse.getBody());
          assertEquals(apiResponse.getStatus().intValue(), HttpStatus.OK.value());
          assertEquals(apiResponse.getBody(), "OK");
     }
     
     @Test
     public void addQueryParamThenSetUrl() {
          Mockito.when(restTemplate.getForEntity(Mockito.any(URI.class), Mockito.any(Class.class))).thenReturn(responseEntity);
          
          ApiResponse apiResponse = subject.queryParam("search", "Heimdall")
                                           .url("https://www.google.com/search")
                                           .sendGet();
          assertNotNull(apiResponse);
          assertNotNull(apiResponse.getStatus());
          assertNotNull(apiResponse.getBody());
          assertEquals(apiResponse.getStatus().intValue(), HttpStatus.OK.value());
          assertEquals(apiResponse.getBody(), "OK");
     }
}
