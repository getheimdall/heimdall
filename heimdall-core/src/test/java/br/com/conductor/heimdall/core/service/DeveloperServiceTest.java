///*-
// * =========================LICENSE_START==================================
// * heimdall-core
// * ========================================================================
// * Copyright (C) 2018 Conductor Tecnologia SA
// * ========================================================================
// * Licensed under the Apache License, Version 2.0 (the "License")
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * ==========================LICENSE_END===================================
// */
//
//package br.com.conductor.heimdall.core.service;
//
//import br.com.conductor.heimdall.core.dto.DeveloperDTO;
//import br.com.conductor.heimdall.core.dto.PageableDTO;
//import br.com.conductor.heimdall.core.dto.page.DeveloperPage;
//import br.com.conductor.heimdall.core.dto.request.DeveloperLogin;
//import br.com.conductor.heimdall.core.entity.Developer;
//import br.com.conductor.heimdall.core.enums.Status;
//import br.com.conductor.heimdall.core.exception.NotFoundException;
//import br.com.conductor.heimdall.core.repository.DeveloperRepository;
//import br.com.conductor.heimdall.core.util.Pageable;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.ExpectedException;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.springframework.data.domain.Example;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//
///**
// * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
// **/
//@RunWith(MockitoJUnitRunner.class)
//public class DeveloperServiceTest {
//
//    @InjectMocks
//    private DeveloperService developerService;
//
//    @Mock
//    private DeveloperRepository developerRepository;
//
//    @Rule
//    public ExpectedException thrown = ExpectedException.none();
//
//    private DeveloperDTO developerDTO;
//    private Developer developer;
//
//    @Before
//    public void initAttributes() {
//        developer = new Developer();
//        developerDTO = new DeveloperDTO();
//
//        developer.setId(1L);
//        developer.setEmail("developer@gmail.com");
//        developer.setName("Developer");
//        developer.setPassword("password");
//        developer.setApps(new ArrayList<>());
//        developer.setStatus(Status.ACTIVE);
//
//        developerDTO.setEmail("developer@gmail.com");
//        developerDTO.setName("Developer");
//        developerDTO.setPassword("password");
//        developerDTO.setStatus(Status.ACTIVE);
//    }
//
//    @Test
//    public void saveWithSuccessTest() {
//        Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);
//        Developer saved = developerService.save(developerDTO);
//
//        assertEquals(developer, saved);
//    }
//
//    @Test
//    public void findWithSuccessTest() {
//        Mockito.when(developerRepository.findOne(developer.getId())).thenReturn(developer);
//        Developer found = developerService.find(this.developer.getId());
//
//        assertEquals(developer, found);
//    }
//
//    @Test
//    public void findWithNotFound() {
//        thrown.expect(NotFoundException.class);
//        thrown.expectMessage("Resource not found");
//
//        Mockito.when(developerRepository.findOne(developer.getId())).thenReturn(null);
//        developerService.find(developer.getId());
//    }
//
//    @Test
//    public void loginWithSuccess() {
//        DeveloperLogin developerLogin = new DeveloperLogin();
//        developerLogin.setEmail(developer.getEmail());
//        developerLogin.setPassword(developer.getPassword());
//
//        Mockito.when(developerRepository.findByEmailAndPassword(developer.getEmail(), developer.getPassword())).thenReturn(developer);
//
//        Developer logged = developerService.login(developerLogin);
//
//        assertEquals(developer, logged);
//    }
//
//    @Test
//    public void loginWithError() {
//        DeveloperLogin developerLogin = new DeveloperLogin();
//        developerLogin.setEmail("other@gmail.com");
//        developerLogin.setPassword("otherPassword");
//
//        Mockito.when(developerRepository.findByEmailAndPassword(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
//        Developer logged = developerService.login(developerLogin);
//
//        assertNull(logged);
//    }
//
//    @Test
//    public void listPageable() {
//        PageableDTO pageableDTO = new PageableDTO();
//        pageableDTO.setLimit(10);
//        pageableDTO.setOffset(0);
//        List<Developer> developers = new ArrayList<>();
//        developers.add(developer);
//
//        Page<Developer> page = new PageImpl<>(developers);
//
//        Mockito.when(developerRepository.findAll(Mockito.any(), Mockito.any(Pageable.class))).thenReturn(page);
//
//        DeveloperPage list = developerService.list(developerDTO, pageableDTO);
//
//        List<Developer> content = list.getContent();
//
//        assertEquals(developers, content);
//    }
//
//    @Test
//    public void listArray() {
//        PageableDTO pageableDTO = new PageableDTO();
//        pageableDTO.setLimit(10);
//        pageableDTO.setOffset(0);
//        List<Developer> developers = new ArrayList<>();
//        developers.add(developer);
//
//        Mockito.when(developerRepository.findAll(Mockito.any(Example.class))).thenReturn(developers);
//        List<Developer> developersResult = developerService.list(developerDTO);
//
//        assertEquals(developers, developersResult);
//    }
//
//    @Test
//    public void updateWithSuccess() {
//        developerDTO.setName("Developer Updated");
//        Mockito.when(developerRepository.findOne(developer.getId())).thenReturn(developer);
//        developer.setName("Developer Updated");
//        Mockito.when(developerRepository.save(Mockito.any(Developer.class))).thenReturn(developer);
//
//        Developer updated = developerService.update(developer.getId(), developerDTO);
//
//        assertEquals(developer, updated);
//    }
//
//    @Test
//    public void updateWithDeveloperNotFound() {
//
//        thrown.expect(NotFoundException.class);
//        thrown.expectMessage("Resource not found");
//
//        Mockito.when(developerRepository.findOne(developer.getId())).thenReturn(null);
//
//        developerService.update(developer.getId(), developerDTO);
//    }
//
//    @Test
//    public void deleteWithSuccess() {
//
//        Mockito.when(developerRepository.findOne(Mockito.anyLong())).thenReturn(developer);
//
//        developerService.delete(developer.getId());
//
//        Mockito.verify(developerRepository, Mockito.times(1)).findOne(Mockito.anyLong());
//    }
//
//    @Test
//    public void deleteWithDeveloperNotFound() {
//
//        thrown.expect(NotFoundException.class);
//        thrown.expectMessage("Resource not found");
//
//        Mockito.when(developerRepository.findOne(Mockito.anyLong())).thenReturn(null);
//        developerService.delete(developer.getId());
//    }
//}
