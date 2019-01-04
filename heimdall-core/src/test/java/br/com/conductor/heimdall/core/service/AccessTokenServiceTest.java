package br.com.conductor.heimdall.core.service;

import static org.junit.Assert.assertEquals;

import br.com.conductor.heimdall.core.entity.Plan;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.dto.persist.AccessTokenPersist;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.exception.NotFoundException;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;

import java.util.Arrays;
import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenServiceTest {

     @InjectMocks
     private AccessTokenService service;
     
     @Mock
     private AccessTokenRepository accessTokenRepository;
     
     @Mock
     private AppRepository appRespository;

     @Mock
     private AMQPCacheService amqpCacheService;
     
     @Rule
     public ExpectedException thrown = ExpectedException.none();
     
     
     @Test
     public void notPermitToSaveTheAccessTokenWithCodeExistent() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("Token already exists");

          AccessTokenPersist persist = new AccessTokenPersist();
          persist.setCode("123456");
          persist.setApp(new ReferenceIdDTO(10L));

          Plan plan = new Plan();
          plan.setId(1L);
          persist.setPlans(Collections.singletonList(new ReferenceIdDTO(1L)));

          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");
          
          App appRecovered = new App();
          appRecovered.setId(10L);
          appRecovered.setPlans(Collections.singletonList(plan));
          
          Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(recoverAt);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(appRecovered);
          
          service.save(persist);
     }
     
     @Test
     public void savingAccessTokenWithCode() {          
          AccessTokenPersist persist = new AccessTokenPersist();
          persist.setCode("123456");
          persist.setApp(new ReferenceIdDTO(10L));

          Plan plan = new Plan();
          plan.setId(1L);
          persist.setPlans(Collections.singletonList(new ReferenceIdDTO(1L)));

          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");
          
          App appRecovered = new App();
          appRecovered.setId(10L);
          appRecovered.setPlans(Collections.singletonList(plan));
          
          Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(null);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(appRecovered);
          Mockito.when(accessTokenRepository.save(Mockito.any(AccessToken.class))).thenReturn(recoverAt);
          
          AccessToken savedAt = service.save(persist);
          
          assertEquals(savedAt.getCode(), persist.getCode());
          Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
     }
     
     @Test
     public void generateAccessTokenWithRandomCode() {
          AccessTokenPersist persist = new AccessTokenPersist();
          persist.setApp(new ReferenceIdDTO(10L));

          Plan plan = new Plan();
          plan.setId(1L);

          persist.setPlans(Collections.singletonList(new ReferenceIdDTO(1L)));
          
          App appRecovered = new App();
          appRecovered.setId(10L);
          appRecovered.setPlans(Collections.singletonList(plan));
          
          Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(null);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(appRecovered);
          
          service.save(persist);
          
          Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
     }
     
     @Test
     public void notPermitToSaveAccessTokenWithInvalidApp() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("App does not exist");
          
          AccessTokenPersist persist = new AccessTokenPersist();
          persist.setApp(new ReferenceIdDTO(10L));
          
          Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(null);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(null);
          
          service.save(persist);
          
          Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
          Mockito.verify(accessTokenRepository, Mockito.times(1)).findByCode(Mockito.anyString());
     }
     
     @Test
     public void callOneRecursiveSaveWhenRandomNumberExistInDatabase() {
          AccessTokenPersist persist = new AccessTokenPersist();
          persist.setApp(new ReferenceIdDTO(10L));

          Plan plan = new Plan();
          plan.setId(1L);
          persist.setPlans(Collections.singletonList(new ReferenceIdDTO(1L)));

          App appRecovered = new App();
          appRecovered.setId(10L);
          appRecovered.setPlans(Collections.singletonList(plan));

          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");
          
          Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(recoverAt).thenReturn(null);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(appRecovered);
          
          service.save(persist);
          
          Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
          Mockito.verify(accessTokenRepository, Mockito.times(2)).findByCode(Mockito.anyString());
     }
     
     @Test
     public void notPermitUpdateAnAccessTokenInexistent() {
          thrown.expect(NotFoundException.class);
          thrown.expectMessage("Resource not found");
          
          AccessTokenPersist accessTokenPersist = new AccessTokenPersist();
          
          Mockito.when(accessTokenRepository.findOne(Mockito.anyLong())).thenReturn(null);
          
          service.update(10L, accessTokenPersist);
     }
     
     @Test
     public void notPermitUpdateAnAccessTokenWithInexistentApp() {
          thrown.expect(BadRequestException.class);
          thrown.expectMessage("App does not exist");
          
          AccessTokenPersist accessTokenPersist = new AccessTokenPersist();
          accessTokenPersist.setApp(new ReferenceIdDTO(10L));
          
          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");
          
          Mockito.when(accessTokenRepository.findOne(Mockito.anyLong())).thenReturn(recoverAt);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(null);
          
          service.update(10L, accessTokenPersist);
     }
     
     @Test
     public void updatingAnAccessTokenWithExistentApp() {         
          AccessTokenPersist accessTokenPersist = new AccessTokenPersist();
          accessTokenPersist.setApp(new ReferenceIdDTO(10L));

          Plan plan = new Plan();
          plan.setId(1L);
          accessTokenPersist.setPlans(Collections.singletonList(new ReferenceIdDTO(1L)));

          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");
          
          App appRecovered = new App();
          appRecovered.setId(10L);
          appRecovered.setPlans(Collections.singletonList(plan));

          Mockito.when(accessTokenRepository.findOne(Mockito.anyLong())).thenReturn(recoverAt);
          Mockito.when(appRespository.findOne(Mockito.anyLong())).thenReturn(appRecovered);
          
          service.update(10L, accessTokenPersist);
          
          Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
     }
}
