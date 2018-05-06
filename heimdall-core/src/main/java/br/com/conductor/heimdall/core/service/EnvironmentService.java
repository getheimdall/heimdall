
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
import static br.com.twsoftware.alfred.object.Objeto.notBlank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.EnvironmentDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.EnvironmentPage;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.EnvironmentRepository;
import br.com.conductor.heimdall.core.util.Pageable;
/**
 * <h1>Environment Service</h1>
 * 
 * This class provides methods to create, read, update and delete the {@link Environment} resource.
 * 
 * @author Filipe Germano
 *
 */
@Service
public class EnvironmentService {

     @Autowired
     private EnvironmentRepository environmentRepository;
     
     /**
      * Finds a {@link Environment} by its ID.
      * 
      * @param 	id 						- The id of the {@link Environment} 
      * @return 						The {@link Environment} that was found
      * @throws NotFoundException 		Resource not found
      */
     public Environment find(Long id) {
          
          Environment environment = environmentRepository.findOne(id);      
          HeimdallException.checkThrow(isBlank(environment), GLOBAL_RESOURCE_NOT_FOUND);
                              
          return environment;
     }
     
     /**
      * Generates a paged list of {@link Environment} from a request.
      * 
      * @param environmentDTO			- The {@link EnvironmentDTO}
      * @param pageableDTO				- The {@link PageableDTO}
      * @return							The paged {@link Environment} list as a {@link EnvironmentPage} object
      */
     public EnvironmentPage list(EnvironmentDTO environmentDTO, PageableDTO pageableDTO) {

          Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);
          
          Example<Environment> example = Example.of(environment, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<Environment> page = environmentRepository.findAll(example, pageable);
          
          EnvironmentPage environmentPage = new EnvironmentPage(PageDTO.build(page));
          
          return environmentPage;
     }

     /**
      * Generates a list of {@link Environment} from a request.
      * 
      * @param environmentDTO			- The {@link EnvironmentDTO}
      * @return							The List<{@link Environment}>
      */
     public List<Environment> list(EnvironmentDTO environmentDTO) {
          
          Environment environment = GenericConverter.mapper(environmentDTO, Environment.class);
          
          Example<Environment> example = Example.of(environment, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          List<Environment> environments = environmentRepository.findAll(example);
          
          return environments;
     }
     
     /**
      * Saves a {@link Environment} in the repository.
      * 
      * @param environmentDTO			- The {@link EnvironmentDTO}
      * @return							The saved {@link Environment}
      * @throws BadRequestException		Inbound URL already exists
      */
     @Transactional
     public Environment save(EnvironmentDTO environmentDTO) {

          Environment environment = environmentRepository.findByInboundURL(environmentDTO.getInboundURL());
          HeimdallException.checkThrow(notBlank(environment), ExceptionMessage.ENVIRONMENT_INBOUND_URL_ALREADY_EXISTS);
          
          environment = GenericConverter.mapper(environmentDTO, Environment.class);
          environment = environmentRepository.save(environment);
          
          return environment;
     }

     /**
      * Updates a {@link Environment} by its ID.
      * 
      * @param 	id 						- The id of the {@link Environment} 
      * @param environmentDTO			- The {@link EnvironmentDTO}
      * @return							The updated {@link Environment}
      * @throws NotFoundException		Resource not found
      * @throws BadRequestException		Inbound URL already exists
      */
     @Transactional
     public Environment update(Long id, EnvironmentDTO environmentDTO) {

          Environment environment = environmentRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(environment), GLOBAL_RESOURCE_NOT_FOUND);

          Environment environmentVerify = environmentRepository.findByInboundURL(environmentDTO.getInboundURL());
          HeimdallException.checkThrow(notBlank(environmentVerify) && environmentVerify.getId() != environment.getId(), ExceptionMessage.ENVIRONMENT_INBOUND_URL_ALREADY_EXISTS);
          
          environment = GenericConverter.mapper(environmentDTO, environment);
          environmentRepository.save(environment);
          
          return environment;
     }
     
     /**
      * Deletes a {@link Environment} by its ID.
      * 
      * @param 	id 						- The id of the {@link Environment}
      * @throws NotFoundException		Resource not found
      */
     @Transactional
     public void delete(Long id) {

          Environment environment = environmentRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(environment), GLOBAL_RESOURCE_NOT_FOUND);
          
          environmentRepository.delete(environment);
     }

}
