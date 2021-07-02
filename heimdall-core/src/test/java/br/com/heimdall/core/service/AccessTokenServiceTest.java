package br.com.heimdall.core.service;

import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.ReferenceIdDTO;
import br.com.heimdall.core.dto.integration.AccessTokenDTO;
import br.com.heimdall.core.dto.integration.AppCallbackDTO;
import br.com.heimdall.core.dto.page.AccessTokenPage;
import br.com.heimdall.core.dto.persist.AccessTokenPersist;
import br.com.heimdall.core.dto.request.AccessTokenRequest;
import br.com.heimdall.core.entity.AccessToken;
import br.com.heimdall.core.entity.App;
import br.com.heimdall.core.entity.Plan;
import br.com.heimdall.core.enums.Status;
import br.com.heimdall.core.exception.BadRequestException;
import br.com.heimdall.core.exception.NotFoundException;
import br.com.heimdall.core.repository.AccessTokenRepository;
import br.com.heimdall.core.repository.AppRepository;
import br.com.heimdall.core.repository.PlanRepository;
import br.com.heimdall.core.service.amqp.AMQPCacheService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AccessTokenServiceTest {

     @InjectMocks
     private AccessTokenService service;
     
     @Mock
     private AccessTokenRepository accessTokenRepository;

     @Mock
     private AppService appService;

     @Mock
     private AppRepository appRespository;

     @Mock
     private PlanRepository planRepository;

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

     @Test
     public void saveAccessTokenWithAppCallbackDTO() {
          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");

          Plan plan = new Plan();

          AccessTokenDTO reqBody = new AccessTokenDTO();
          reqBody.setCode("developer@portal.com.br");
          reqBody.setStatus(Status.ACTIVE);
          reqBody.setApp(new AppCallbackDTO());

          Mockito.when(accessTokenRepository.save(Mockito.any(AccessToken.class))).thenReturn(recoverAt);
          Mockito.when(accessTokenRepository.findByCode("123456")).thenReturn(recoverAt);
          Mockito.when(planRepository.findOne(1L)).thenReturn(plan);

          AccessToken saved = this.service.save(reqBody);

          assertEquals(recoverAt.getId(), saved.getId());
     }

     @Test
     public void listAccessTokensTest() {
          AccessToken token = new AccessToken();

          AccessTokenRequest accessTokenRequest = new AccessTokenRequest();

          List<AccessToken> tokens = new ArrayList<>();
          tokens.add(token);

          Mockito.when(this.accessTokenRepository.findAll(Mockito.any(Example.class))).thenReturn(tokens);

          List<AccessToken> tokensResp = this.service.list(accessTokenRequest);

          assertEquals(tokens.size(), tokensResp.size());
          Mockito.verify(this.accessTokenRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
     }

     @Test
     public void listAccessTokensWithPageableTest() {

          PageableDTO pageableDTO = new PageableDTO();
          pageableDTO.setLimit(10);
          pageableDTO.setOffset(0);

          ArrayList<AccessToken> listTokens = new ArrayList<>();

          AccessToken token = new AccessToken();
          AccessTokenRequest accessTokenRequest = new AccessTokenRequest();

          listTokens.add(token);

          Page<AccessToken> page = new PageImpl<>(listTokens);

          Mockito.when(this.accessTokenRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
                 .thenReturn(page);

          AccessTokenPage planPageResp = this.service.list(accessTokenRequest, pageableDTO);

          Assert.assertEquals(1L, planPageResp.getTotalElements());
          Mockito.verify(this.accessTokenRepository, Mockito.times(1))
                 .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
     }

     @Test
     public void findAccessTokenTest() {
          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");

          Mockito.when(this.accessTokenRepository.findOne(Mockito.any(Long.class))).thenReturn(recoverAt);
          AccessToken accessTokenResp = service.find(1L);
          assertEquals(accessTokenResp.getId(), recoverAt.getId());
          Mockito.verify(this.accessTokenRepository, Mockito.times(1)).findOne(Mockito.any(Long.class));
     }

     @Test
     public void deleteAnAccessToken() {
          AccessToken recoverAt = new AccessToken();
          recoverAt.setCode("123456");

          Mockito.when(accessTokenRepository.findOne(Mockito.anyLong())).thenReturn(recoverAt);
          this.service.delete(1L);
          Mockito.verify(this.accessTokenRepository, Mockito.times(1)).delete(recoverAt);

     }

}
