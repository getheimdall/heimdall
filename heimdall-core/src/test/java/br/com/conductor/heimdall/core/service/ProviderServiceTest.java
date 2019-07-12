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

import br.com.conductor.heimdall.core.entity.Provider;
import br.com.conductor.heimdall.core.entity.ProviderParam;
import br.com.conductor.heimdall.core.exception.ForbiddenException;
import br.com.conductor.heimdall.core.repository.ProviderRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ProviderServiceTest {

    @InjectMocks
    private ProviderService providerService;

    @Mock
    private ProviderRepository providerRepository;

    private Provider providerDTO;

    private Provider providerDatabase;

    @Before
    public void initFeatures() {
        this.providerDTO = new Provider();

        ProviderParam providerParams = new ProviderParam();

        providerParams.setName("user");
        providerParams.setValue("admin");
        providerParams.setLocation("HEADER");

        List<ProviderParam> listProviderParams = new ArrayList<>();

        listProviderParams.add(providerParams);

        this.providerDTO.setName("Provider Authentication");
        this.providerDTO.setDescription("Provider to authenticate user.");
        this.providerDTO.setPath("http://api.com.br/authentication");
        this.providerDTO.setProviderParams(listProviderParams);

        this.providerDatabase = new Provider();
        this.providerDatabase.setId("1L");
        this.providerDatabase.setName("Prov Authentication");
        this.providerDatabase.setProviderDefault(false);
    }

    @Test
    public void testSaveProvider() {

        Mockito.when(this.providerRepository.save(Mockito.any(Provider.class))).thenReturn(this.providerDatabase);

        Provider provider = this.providerService.save(this.providerDTO);

        assertNotNull(provider.getId());
        Mockito.verify(this.providerRepository, Mockito.times(1)).save(Mockito.any(Provider.class));
    }

    @Test
    public void testEditProvider() {
        this.providerDatabase
                .setProviderParams(providerDTO.getProviderParams().stream()
                        .map(p -> providerService.getProviderParam(p, providerDatabase))
                        .collect(Collectors.toList()));
        Mockito.when(this.providerRepository.findById("1L")).thenReturn(Optional.of(providerDatabase));
        Mockito.when(providerRepository.save(Mockito.any(Provider.class))).thenReturn(providerDatabase);

        this.providerDTO.setName("Prov Authentication");
        Provider providerResp = this.providerService.edit("1L", this.providerDTO);

        assertEquals(this.providerDatabase.getName(), providerResp.getName());
        Mockito.verify(this.providerRepository, Mockito.times(1)).save(Mockito.any(Provider.class));
    }

    @Test
    public void testListWithPageableAndFilter() {

        Pageable pageable = PageRequest.of(0, 10);

        ArrayList<Provider> listProviders = new ArrayList<>();

        this.providerDatabase.setName("Provider Authentication");

        listProviders.add(providerDatabase);

        Page<Provider> page = new PageImpl<>(listProviders);

        Mockito.when(this.providerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<Provider> providerPageResp = this.providerService.list(pageable);

        assertEquals(1L, providerPageResp.getTotalElements());
        Mockito.verify(this.providerRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    public void testListWithFilter() {

        List<Provider> listProviders = new ArrayList<>();
        this.providerDatabase.setName("Provider Authentication");
        listProviders.add(providerDatabase);

        Mockito.when(providerRepository.findAll()).thenReturn(listProviders);

        List<Provider> listProvidersResp = this.providerService.list();

        assertEquals(listProviders.size(), listProvidersResp.size());
        Mockito.verify(this.providerRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void testDelete() {
        Mockito.when(this.providerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(new Provider()));

        this.providerService.delete("1L");
        Mockito.verify(this.providerRepository, Mockito.times(1)).delete(Mockito.any(Provider.class));
    }

    @Test(expected = ForbiddenException.class)
    public void testUpdateProviderWhenDefaultIsTrue() {
        providerDatabase.setProviderDefault(true);
        Mockito.when(this.providerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(providerDatabase));

        providerService.edit("1L", providerDTO);
    }

    @Test
    public void findProviderTest() {
        Provider provider = new Provider();

        Mockito.when(this.providerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(provider));
        Provider accessTokenResp = providerService.find("1L");
        assertEquals(accessTokenResp.getId(), provider.getId());
        Mockito.verify(this.providerRepository, Mockito.times(1)).findById(Mockito.anyString());
    }
}
