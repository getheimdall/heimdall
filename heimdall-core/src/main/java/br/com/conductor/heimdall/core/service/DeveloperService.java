
package br.com.conductor.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;

import java.util.List;

import br.com.conductor.heimdall.core.dto.request.DeveloperLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.DeveloperDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.DeveloperPage;
import br.com.conductor.heimdall.core.entity.Developer;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.DeveloperRepository;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * This class provides methods to create, read, update and delete the {@link Developer} resource.
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Service
public class DeveloperService {

     @Autowired
     private DeveloperRepository developerRepository;

     /**
      * Finds a {@link Developer} by its ID.
      * 
      * @param  id 						The ID of the {@link Developer} 
      * @return 						The {@link Developer} found
      * @throws NotFoundException		Resource not found
      */
     public Developer find(Long id) {
          
          Developer developer = developerRepository.findOne(id);      
          HeimdallException.checkThrow(isBlank(developer), GLOBAL_RESOURCE_NOT_FOUND);
                              
          return developer;
     }

     /**
      * Finds a {@link Developer} by its email and password.
      *
      * @param developerLogin The {@link DeveloperLogin}
      * @return The Developer
      */
     public Developer login(DeveloperLogin developerLogin) {

          return developerRepository.findByEmailAndPassword(developerLogin.getEmail(), developerLogin.getPassword());
     }
     
     /**
      * Generates a paged list of {@link Developer}s from a request.
      * 
      * @param  developerDTO 			The {@link DeveloperDTO}
      * @param  pageableDTO 			The {@link PageableDTO}
      * @return							The paged {@link Developer} list as a {@link DeveloperPage} object
      */
     public DeveloperPage list(DeveloperDTO developerDTO, PageableDTO pageableDTO) {

          Developer developer = GenericConverter.mapper(developerDTO, Developer.class);
          
          Example<Developer> example = Example.of(developer, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<Developer> page = developerRepository.findAll(example, pageable);
          
          DeveloperPage developerPage = new DeveloperPage(PageDTO.build(page));
          
          return developerPage;
     }

     /**
      * Generates a list of {@link Developer} from a request.
      * 
      * @param  developerDTO 			The {@link DeveloperDTO}
      * @return							The list of {@link Developer}
      */
     public List<Developer> list(DeveloperDTO developerDTO) {
          
          Developer developer = GenericConverter.mapper(developerDTO, Developer.class);
          
          Example<Developer> example = Example.of(developer, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          List<Developer> developers = developerRepository.findAll(example);
          
          return developers;
     }
     
     /**
      * Saves a {@link Developer}.
      * 
      * @param  developerDTO 			The {@link DeveloperDTO}
      * @return							The {@link Developer} saved
      */
     @Transactional
     public Developer save(DeveloperDTO developerDTO) {

          Developer developer = GenericConverter.mapper(developerDTO, Developer.class);
          developer = developerRepository.save(developer);
          
          return developer;
     }

     /**
      * Updates a {@link Developer} by its ID.
      * 
      * @param  id						The ID of the {@link Developer} to be updated
      * @param  developerDTO 			The {@link DeveloperDTO}
      * @return							The updated {@link Developer}
      * @throws NotFoundException		Resource not found
      */
     @Transactional
     public Developer update(Long id, DeveloperDTO developerDTO) {

          Developer developer = developerRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(developer), GLOBAL_RESOURCE_NOT_FOUND);
          
          developer = GenericConverter.mapper(developerDTO, developer);
          developer = developerRepository.save(developer);
          
          return developer;
     }
     
     /**
      * Deletes a {@link Developer} by its ID.
      * @param  id						The ID of the {@link Developer} to be deleted
      * @throws NotFoundException		Resource not found
      */
     @Transactional
     public void delete(Long id) {

          Developer developer = developerRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(developer), GLOBAL_RESOURCE_NOT_FOUND);
          
          developerRepository.delete(developer);
     }

}
