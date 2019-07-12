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

import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.request.DeveloperLogin;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.enums.Status;
import br.com.conductor.heimdall.core.exception.NotFoundException;
import br.com.conductor.heimdall.core.repository.DeveloperRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 **/
@RunWith(MockitoJUnitRunner.class)
public class DeveloperServiceTest {

    @InjectMocks
    private DeveloperService developerService;

    @Mock
    private DeveloperRepository developerRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Developer developerDTO;
    private Developer developer;

    @Before
    public void initAttributes() {
        developer = new Developer();
        developerDTO = new Developer();

        developer.setId("1L");
        developer.setEmail("developer@gmail.com");
        developer.setName("Developer");
        developer.setPassword("password");
        developer.setStatus(Status.ACTIVE);

        developerDTO.setEmail("developer@gmail.com");
        developerDTO.setName("Developer");
        developerDTO.setPassword("password");
        developerDTO.setStatus(Status.ACTIVE);
    }

    @Test
    public void saveWithSuccessTest() {
        Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);
        Developer saved = developerService.save(developerDTO);

        assertEquals(developer, saved);
    }

    @Test
    public void findWithSuccessTest() {
        Mockito.when(developerRepository.findById(developer.getId())).thenReturn(Optional.of(developer));
        Developer found = developerService.find(this.developer.getId());

        assertEquals(developer, found);
    }

    @Test
    public void findWithNotFound() {
        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Developer not found");

        Mockito.when(developerRepository.findById(developer.getId())).thenReturn(Optional.empty());
        developerService.find(developer.getId());
    }

    @Test
    public void loginWithSuccess() {
        DeveloperLogin developerLogin = new DeveloperLogin();
        developerLogin.setEmail(developer.getEmail());
        developerLogin.setPassword(developer.getPassword());

        Mockito.when(developerRepository.findByEmailAndPassword(developer.getEmail(), developer.getPassword())).thenReturn(developer);

        Developer logged = developerService.login(developerLogin);

        assertEquals(developer, logged);
    }

    @Test
    public void loginWithError() {
        DeveloperLogin developerLogin = new DeveloperLogin();
        developerLogin.setEmail("other@gmail.com");
        developerLogin.setPassword("otherPassword");

        Mockito.when(developerRepository.findByEmailAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
        Developer logged = developerService.login(developerLogin);

        assertNull(logged);
    }

    @Test
    public void listPageable() {

        Pageable pageable = PageRequest.of(0, 10);

        List<Developer> developers = new ArrayList<>();
        developers.add(developer);

        Page<Developer> page = new PageImpl<>(developers);

        Mockito.when(developerRepository.findAll(Mockito.any(Pageable.class))).thenReturn(page);

        Page<Developer> list = developerService.list(pageable);

        assertEquals(developers, list.getContent());
    }

    @Test
    public void listArray() {
        PageableDTO pageableDTO = new PageableDTO();
        pageableDTO.setLimit(10);
        pageableDTO.setPage(0);
        List<Developer> developers = new ArrayList<>();
        developers.add(developer);

        Mockito.when(developerRepository.findAll()).thenReturn(developers);
        List<Developer> developersResult = developerService.list();

        assertEquals(developers, developersResult);
    }

    @Test
    public void updateWithSuccess() {
        developerDTO.setName("Developer Updated");
        Mockito.when(developerRepository.findById(developer.getId())).thenReturn(Optional.of(developer));
        developer.setName("Developer Updated");
        Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);

        Developer updated = developerService.update(developer.getId(), developerDTO);

        assertEquals(developer, updated);
    }

    @Test
    public void updateWithDeveloperNotFound() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Developer not found");

        Mockito.when(developerRepository.findById(developer.getId())).thenReturn(Optional.empty());

        developerService.update(developer.getId(), developerDTO);
    }

    @Test
    public void deleteWithSuccess() {

        Mockito.when(developerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(developer));

        developerService.delete(developer.getId());

        Mockito.verify(developerRepository, Mockito.times(1)).findById(Mockito.anyString());
    }

    @Test
    public void deleteWithDeveloperNotFound() {

        thrown.expect(NotFoundException.class);
        thrown.expectMessage("Developer not found");

        Mockito.when(developerRepository.findById(developer.getId())).thenReturn(Optional.empty());
        developerService.delete(developer.getId());
    }
}
