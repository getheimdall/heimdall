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
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.exception.BadRequestException;
import br.com.conductor.heimdall.core.repository.AppRepository;
import org.assertj.core.util.Lists;
import org.mockito.internal.util.collections.Sets;
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

import static org.junit.Assert.*;

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
    private DeveloperService developerService;

    @Mock
    private AccessTokenService accessTokenService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private App app;
    private App app1;
    private Developer developer;
    private App appPersist;
    private App appUpdateDTO;

    @Before
    public void initAttributes() {
        app = new App();
        app.setId("1L");
        app.setClientId("0a9s8df76g");

        app1 = new App();
        app1.setId("2L");
        app1.setClientId("0q9w8e7r6t");

        developer = new Developer();
        developer.setId("1L");
        developer.setName("dev");
        developer.setEmail("dev@email.com");

        appPersist = new App();
        appPersist.setName("App test");
        appPersist.setDescription("App test description");
        appPersist.setDeveloperId("1L");

        appUpdateDTO = new App();
        appUpdateDTO.setName("App name");
        appUpdateDTO.setDeveloperId("1L");
    }

    @Test
    public void testSaveWithCodeAutoGenerate() {

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(developerService.find(Mockito.anyString())).thenReturn(developer);

        App saved = this.appService.save(appPersist);

        assertEquals(app.getId(), saved.getId());
    }

    @Test
    public void testSaveWithCodeInformed() {

        appPersist.setClientId("8d19n91d8");

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(developerService.find(Mockito.anyString())).thenReturn(developer);

        App saved = this.appService.save(appPersist);

        assertEquals(app.getId(), saved.getId());
    }

    @Test
    public void testSaveWithCodeIsAlready() {

        thrown.expect(BadRequestException.class);
        thrown.expectMessage("Client Id already used");

        appPersist.setClientId("0q9w8e7r6t");

        Mockito.when(appRepository.findByClientId(Mockito.anyString())).thenReturn(null);
        Mockito.when(appRepository.findByClientId("0q9w8e7r6t")).thenReturn(app1);

        this.appService.save(appPersist);
    }


    @Test
    public void listAppWithPageableTest() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<App> page = new PageImpl<>(Lists.newArrayList(app));

        Mockito.when(appRepository.findAll(pageable)).thenReturn(page);

        Page<App> apiPageResp = appService.list(pageable);

        assertEquals(1L, apiPageResp.getTotalElements());
        Mockito.verify(this.appRepository, Mockito.times(1))
                .findAll(Mockito.any(Pageable.class));
    }

    @Test
    public void listAppWithoutPageableTest() {

        List<App> apps = Lists.newArrayList(app);

        Mockito.when(this.appRepository.findAll()).thenReturn(apps);
        List<App> listAppResp = appService.list();

        assertEquals(apps.size(), listAppResp.size());
        Mockito.verify(this.appRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findAppTest() {

        Mockito.when(this.appRepository.findById(Mockito.anyString())).thenReturn(Optional.of(app));
        Mockito.when(accessTokenService.findByAppId(app.getId())).thenReturn(Mockito.anyList());
        App appResp = appService.find("1L");
        assertEquals(appResp.getId(), app.getId());
        Mockito.verify(this.appRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void updateAppTest() {

        Mockito.when(developerService.find(Mockito.anyString())).thenReturn(developer);
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);

        App saved = appService.save(appPersist);

        assertEquals(saved.getId(), app.getId());

        String plan1 = "1L";

        String plan2 = "2L";

        Set<String> plans = Sets.newSet(plan1, plan2);

        app.setPlans(plans);

        AccessToken act1 = new AccessToken();
        act1.setId("1L");
        act1.setApp(app.getId());
        act1.setPlans(plans);

        AccessToken act2 = new AccessToken();
        act2.setId("2L");
        act2.setApp(app.getId());
        act2.setPlans(plans);

        Set<String> accessTokens = Sets.newSet(act1.getId(), act2.getId());
        List<AccessToken> accessTokensReturned = Lists.newArrayList(act1, act2);

        app.setAccessTokens(accessTokens);

        Mockito.when(appRepository.findById(Mockito.anyString())).thenReturn(Optional.of(app));
        Mockito.when(accessTokenService.findByAppId(app.getId())).thenReturn(accessTokensReturned);

        App appDTO = new App();
        Set<String> referenceIdDTOS = Sets.newSet("1L");
        appDTO.setPlans(referenceIdDTOS);

        App update = appService.update("1L", appDTO);

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

        Mockito.when(this.appRepository.findById(Mockito.anyString())).thenReturn(Optional.of(app));
        this.appService.delete("1L");
        Mockito.verify(this.appRepository, Mockito.times(1)).delete(app);

    }

    @Test
    public void testUpdateHavingPlans() {
        List<AccessToken> accessTokens = new ArrayList<>();
        Set<String> plans = new HashSet<>();
        plans.add("1L");

        AccessToken accessToken = new AccessToken();
        accessToken.setPlans(plans);
        accessToken.setId("0L");

        accessTokens.add(accessToken);

        Mockito.when(this.appRepository.findById(Mockito.anyString())).thenReturn(Optional.of(app));
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(accessTokenService.findByAppId(Mockito.anyString())).thenReturn(accessTokens);

        this.appService.update("0L", appUpdateDTO);
        Mockito.verify(accessTokenService, Mockito.times(3)).findByAppId(Mockito.anyString());
        Mockito.verify(accessTokenService, Mockito.times(1)).update(Mockito.any(AccessToken.class));
    }

    @Test
    public void testUpdateHavingNoAccessToken() {
        Mockito.when(appRepository.findById(Mockito.anyString())).thenReturn(Optional.of(app));
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(accessTokenService.findByAppId(Mockito.anyString())).thenReturn(null);

        this.appService.update("0L", appUpdateDTO);
        Mockito.verify(accessTokenService, Mockito.times(0)).save(Mockito.any(AccessToken.class));
    }

    @Test
    public void testUpdateHavingEmptyAccessToken() {
        Mockito.when(appRepository.findById(Mockito.anyString())).thenReturn(Optional.of(app));
        Mockito.when(appRepository.save(Mockito.any(App.class))).thenReturn(app);
        Mockito.when(accessTokenService.findByAppId(Mockito.anyString())).thenReturn(new ArrayList<>());

        this.appService.update("0L", appUpdateDTO);
        Mockito.verify(accessTokenService, Mockito.times(0)).save(Mockito.any(AccessToken.class));
    }

}
