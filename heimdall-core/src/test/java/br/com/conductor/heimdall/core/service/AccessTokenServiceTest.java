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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.exception.NotFoundException;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

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

    private App app;

    private Plan plan;

    private Set<String> plans;

    @Before
    public void setUp() {
        this.app = new App("10L", null, null, null, null, null, null, null, null);
        this.plan = new Plan("1L", null, null, null, null, false, null, null);
        this.plans = new HashSet<>();
        this.plans.add(plan.getId());
    }

    @Test
    public void notPermitToSaveTheAccessTokenWithCodeExistent() {
        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Access Token already used");

        AccessToken persist = new AccessToken();
        persist.setCode("123456");
        persist.setApp(this.app.getId());

        Set<String> plans = new HashSet<>();
        plans.add("1L");

        persist.setPlans(plans);

        AccessToken recoverAt = new AccessToken();
        recoverAt.setCode("123456");

        App appRecovered = new App();
        appRecovered.setId("10L");
        appRecovered.setPlans(plans);

        Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(recoverAt);
        Mockito.when(appService.find(persist.getApp())).thenReturn(appRecovered);

        service.save(persist);
    }

    @Test
    public void savingAccessTokenWithCode() {
        AccessToken persist = new AccessToken();
        persist.setCode("123456");
        persist.setApp(this.app.getId());

        persist.setPlans(plans);

        AccessToken recoverAt = new AccessToken();
        recoverAt.setCode("123456");

        Set<String> plans = new HashSet<>();
        plans.add("1L");

        App appRecovered = new App();
        appRecovered.setId("10L");
        appRecovered.setPlans(plans);

        Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(null);
        Mockito.when(appService.find(persist.getApp())).thenReturn(appRecovered);
        Mockito.when(accessTokenRepository.save(Mockito.any(AccessToken.class))).thenReturn(recoverAt);

        AccessToken savedAt = service.save(persist);

        assertEquals(savedAt.getCode(), persist.getCode());
        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
    }

    @Test
    public void generateAccessTokenWithRandomCode() {
        AccessToken persist = new AccessToken();
        persist.setApp(this.app.getId());

        persist.setPlans(this.plans);

        App appRecovered = new App();
        appRecovered.setId("10L");
        appRecovered.setPlans(this.plans);

        Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(null);
        Mockito.when(accessTokenRepository.save(persist)).thenReturn(persist);
        Mockito.when(appService.find(persist.getApp())).thenReturn(appRecovered);

        service.save(persist);

        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
    }


    @Test
    public void callOneRecursiveSaveWhenRandomNumberExistInDatabase() {
        AccessToken persist = new AccessToken();
        persist.setApp(this.app.getId());

        persist.setPlans(this.plans);

        App appRecovered = new App();
        appRecovered.setId("10L");
        appRecovered.setPlans(this.plans);

        AccessToken recoverAt = new AccessToken();
        recoverAt.setCode("123456");

        Mockito.when(accessTokenRepository.findByCode(Mockito.anyString())).thenReturn(recoverAt).thenReturn(null);
        Mockito.when(appService.find(Mockito.anyString())).thenReturn(appRecovered);
        Mockito.when(accessTokenRepository.save(persist)).thenReturn(persist);

        service.save(persist);

        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
        Mockito.verify(accessTokenRepository, Mockito.times(2)).findByCode(Mockito.anyString());
    }

    @Test
    public void notPermitUpdateAnAccessTokenInexistent() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Access Token not found");

        AccessToken accessToken = new AccessToken();

        Mockito.when(accessTokenRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        service.update("10L", accessToken);
    }

    @Test
    public void updatingAnAccessTokenWithExistentApp() {
        AccessToken accessToken = new AccessToken();
        accessToken.setApp(this.app.getId());

        Set<String> plan = new HashSet<>();
        plan.add("1L");
        accessToken.setPlans(this.plans);

        AccessToken recoverAt = new AccessToken();
        recoverAt.setCode("123456");

        App appRecovered = new App();
        appRecovered.setId("10L");
        appRecovered.setPlans(plan);

        Mockito.when(accessTokenRepository.findById(Mockito.anyString())).thenReturn(Optional.of(recoverAt));
        Mockito.when(appService.find(Mockito.anyString())).thenReturn(appRecovered);

        service.update("10L", accessToken);

        Mockito.verify(accessTokenRepository, Mockito.times(1)).save(Mockito.any(AccessToken.class));
    }

    @Test
    public void listAccessTokensTest() {
        AccessToken token = new AccessToken();

        List<AccessToken> tokens = new ArrayList<>();
        tokens.add(token);

        Mockito.when(this.accessTokenRepository.findAll()).thenReturn(tokens);

        List<AccessToken> tokensResp = this.service.list();

        assertEquals(tokens.size(), tokensResp.size());
        Mockito.verify(this.accessTokenRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void listAccessTokensWithPageableTest() {

        Pageable pageable = PageRequest.of(0, 10);

        List<AccessToken> listTokens = new ArrayList<>();
        AccessToken token = new AccessToken();
        listTokens.add(token);

        Page<AccessToken> page = new PageImpl<>(listTokens, pageable, listTokens.size());

        Mockito.when(accessTokenRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<AccessToken> planPageResp = this.service.list(pageable);

        assertEquals(1L, planPageResp.getTotalElements());
        Mockito.verify(this.accessTokenRepository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));
    }

    @Test
    public void findAccessTokenTest() {
        AccessToken recoverAt = new AccessToken();
        recoverAt.setCode("123456");

        Mockito.when(this.accessTokenRepository.findById(Mockito.anyString())).thenReturn(Optional.of(recoverAt));
        AccessToken accessTokenResp = service.find("1L");
        assertEquals(accessTokenResp.getId(), recoverAt.getId());
        Mockito.verify(this.accessTokenRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void deleteAnAccessToken() {
        AccessToken recoverAt = new AccessToken();
        recoverAt.setCode("123456");

        Mockito.when(accessTokenRepository.findById(Mockito.anyString())).thenReturn(Optional.of(recoverAt));
        this.service.delete("1L");
        Mockito.verify(this.accessTokenRepository, Mockito.times(1)).delete(recoverAt);

    }

}
