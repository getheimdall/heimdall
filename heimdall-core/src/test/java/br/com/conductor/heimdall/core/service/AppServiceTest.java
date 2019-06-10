package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */

import br.com.conductor.heimdall.core.dto.AppDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.dto.integration.AppCallbackDTO;
import br.com.conductor.heimdall.core.dto.page.AppPage;
import br.com.conductor.heimdall.core.dto.persist.AppPersist;
import br.com.conductor.heimdall.core.dto.request.AppRequestDTO;
import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.repository.DeveloperRepository;
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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class AppServiceTest {

    @InjectMocks
    private AppService appService;

    @Mock
    private AppRepository appRepository;

    @Mock
    private DeveloperRepository devRepository;

    @Mock
    private AccessTokenRepository accessTokenRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private AMQPCacheService amqpCacheService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private App app;
    private App app1;
    private Developer developer;
    private AppPersist appPersist;

    @Before
    public void initAttributes() {
        app = new App();
        app.setId(1L);
        app.setClientId("0a9s8df76g");

        app1 = new App();
        app1.setId(2L);
        app1.setClientId("0q9w8e7r6t");

        developer = new Developer();
        developer.setId(1L);
        developer.setName("dev");
        developer.setEmail("dev@email.com");

        appPersist = new AppPersist();
        appPersist.setName("App test");
        appPersist.setDescription("App test description");
        appPersist.setDeveloper(new ReferenceIdDTO(1L));

    }

    @Test
    public void testSaveWithCodeAutoGenerate() {

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.findByClientId("0q9w8e7r6t")).thenReturn(app1);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(devRepository.findOne(Mockito.anyLong())).thenReturn(developer);

        App saved = this.appService.save(appPersist);

        assertEquals(app.getId(), saved.getId());
    }

    @Test
    public void testSaveWithCodeInformed() {

        appPersist.setClientId("8d19n91d8");

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.findByClientId("0q9w8e7r6t")).thenReturn(app1);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(devRepository.findOne(Mockito.anyLong())).thenReturn(developer);

        App saved = this.appService.save(appPersist);

        assertEquals(app.getId(), saved.getId());
    }

    @Test
    public void testSaveWithCodeIsAlready() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("clientId already used");

        appPersist.setClientId("0q9w8e7r6t");

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.findByClientId("0q9w8e7r6t")).thenReturn(app1);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(devRepository.findOne(Mockito.anyLong())).thenReturn(developer);

        this.appService.save(appPersist);
    }

    @Test
    public void SaveWithAppCallbackDTOTest() {

        AppCallbackDTO reqBody = new AppCallbackDTO();
        reqBody.setName("Test Name");
        reqBody.setDeveloper("developer@conductor.com.br");

        appPersist.setClientId("8d19n91d8");

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(devRepository.findOne(Mockito.anyLong())).thenReturn(developer);
        Mockito.when(devRepository.findByEmail(Mockito.anyString())).thenReturn(developer);

        App saved = this.appService.save(reqBody);

        assertEquals(app.getId(), saved.getId());
    }

    @Test
    public void SaveWithAppNotNullCallbackDTOTest() {

        AppCallbackDTO reqBody = new AppCallbackDTO();
        reqBody.setName("Test Name");
        reqBody.setDeveloper("developer@conductor.com.br");

        appPersist.setClientId("8d19n91d8");

        Mockito.when(planRepository.findOne(1L)).thenReturn(new Plan());
        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(app);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(devRepository.findOne(Mockito.anyLong())).thenReturn(developer);
        Mockito.when(devRepository.findByEmail(Mockito.anyString())).thenReturn(developer);

        App saved = this.appService.save(reqBody);

        assertEquals(app.getId(), saved.getId());
    }

    @Test
    public void listAppWithPageableTest() {

        PageableDTO pageableDTO = new PageableDTO();
        pageableDTO.setLimit(10);
        pageableDTO.setOffset(0);

        List<App> apps = new ArrayList<>();
        apps.add(app);

        AppRequestDTO appRequestDTO = new AppRequestDTO();

        Page<App> page = new PageImpl<>(apps);

        Mockito.when(this.appRepository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class)))
               .thenReturn(page);

        AppPage apiPageResp = appService.list(appRequestDTO, pageableDTO);

        assertEquals(1L, apiPageResp.getTotalElements());
        Mockito.verify(this.appRepository, Mockito.times(1))
               .findAll(Mockito.any(Example.class), Mockito.any(Pageable.class));
    }

    @Test
    public void listAppWithoutPageableTest() {

        List<App> apps = new ArrayList<>();
        apps.add(app);

        Mockito.when(this.appRepository.findAll(Mockito.any(Example.class))).thenReturn(apps);
        AppRequestDTO appRequestDTO = new AppRequestDTO();
        List<App> listAppResp = appService.list(appRequestDTO);

        assertEquals(apps.size(), listAppResp.size());
        Mockito.verify(this.appRepository, Mockito.times(1)).findAll(Mockito.any(Example.class));
    }

    @Test
    public void findAppTest() {

        Mockito.when(this.appRepository.findOne(Mockito.any(Long.class))).thenReturn(app);
        Mockito.when(accessTokenRepository.findByAppId(app.getId())).thenReturn(Mockito.anyList());
        App appResp = appService.find(1L);
        assertEquals(appResp.getId(), app.getId());
        Mockito.verify(this.appRepository, Mockito.times(1)).findOne(Mockito.any(Long.class));
    }

    @Test
    public void updateAppTest() {

        Mockito.when(devRepository.findOne(Mockito.anyLong())).thenReturn(developer);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);

        App saved = appService.save(appPersist);

        assertEquals(saved.getId(), app.getId());

        Plan plan1 = new Plan();
        plan1.setId(1L);

        Plan plan2 = new Plan();
        plan2.setId(2L);

        List<Plan> plans = new ArrayList<>();
        plans.add(plan1);
        plans.add(plan2);
        app.setPlans(plans);

        List<AccessToken> accessTokens = new ArrayList<>();
        AccessToken act1 = new AccessToken();
        act1.setId(1L);
        act1.setApp(app);
        act1.setPlans(plans);

        AccessToken act2 = new AccessToken();
        act2.setId(2L);
        act2.setApp(app);
        act2.setPlans(plans);

        accessTokens.add(act1);
        accessTokens.add(act2);

        app.setAccessTokens(accessTokens);

        Mockito.when(appRepository.findOne(Mockito.anyLong())).thenReturn(app);
        Mockito.when(accessTokenRepository.findByAppId(app.getId())).thenReturn(accessTokens);

        AppDTO appDTO = new AppDTO();
        List<ReferenceIdDTO> referenceIdDTOS = new ArrayList<>();
        ReferenceIdDTO referenceIdDTO = new ReferenceIdDTO();
        referenceIdDTO.setId(1L);
        referenceIdDTOS.add(referenceIdDTO);
        appDTO.setPlans(referenceIdDTOS);

        App update = appService.update(1L, appDTO);

        assertEquals(update.getId(), app.getId());
        assertEquals(1, act1.getPlans().size());
        assertEquals(1, act2.getPlans().size());
        assertTrue(act1.getPlans().contains(plan1));
        assertTrue(act2.getPlans().contains(plan1));
        assertFalse(act1.getPlans().contains(plan2));
        assertFalse(act2.getPlans().contains(plan2));
    }

    @Test
    public void deleteAppTest() {

        Mockito.when(appRepository.findOne(Mockito.anyLong())).thenReturn(app);
        this.appService.delete(1L);
        Mockito.verify(this.appRepository, Mockito.times(1)).delete(app);

    }
}
