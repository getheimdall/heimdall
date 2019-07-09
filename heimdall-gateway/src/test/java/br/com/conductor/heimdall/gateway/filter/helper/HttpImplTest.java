package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.gateway.configuration.TimeoutCounter;
import br.com.conductor.heimdall.gateway.failsafe.CircuitBreakerManager;
import br.com.conductor.heimdall.middleware.spec.ApiResponse;
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

import java.util.concurrent.Callable;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class HttpImplTest {

     @InjectMocks
     private HttpImpl subject = new HttpImpl(new RestTemplate(), new CircuitBreakerManager(), true,
                                             new TimeoutCounter());

     @Mock
     private CircuitBreakerManager circuitBreakerManager;

     private ResponseEntity<String> responseEntity;

     @Before
     public void setup() {
          responseEntity = ResponseEntity.ok("OK");
     }

     @Test
     public void sendGetWithNoParams() {
          Mockito.when(circuitBreakerManager.failsafe(Mockito.any(Callable.class), Mockito.anyString())).thenReturn(responseEntity);

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
          Mockito.when(circuitBreakerManager.failsafe(Mockito.any(Callable.class), Mockito.anyString())).thenReturn(responseEntity);

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
          Mockito.when(circuitBreakerManager.failsafe(Mockito.any(Callable.class), Mockito.anyString())).thenReturn(responseEntity);

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
          Mockito.when(circuitBreakerManager.failsafe(Mockito.any(Callable.class), Mockito.anyString())).thenReturn(responseEntity);

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
