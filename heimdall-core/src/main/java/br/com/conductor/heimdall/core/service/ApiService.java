
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

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.API_BASEPATH_EXIST;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.API_BASEPATH_MALFORMED;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.API_BASEPATH_EMPTY;
import static br.com.twsoftware.alfred.object.Objeto.isBlank;
import static br.com.twsoftware.alfred.object.Objeto.notBlank;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.core.converter.ApiMap;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.ApiDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.page.ApiPage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * This class provides methods to create, read, update and delete the {@link Api} resource.
 * 
 * @author Filipe Germano
 *
 */
@Service
public class ApiService {

     @Autowired
     private ApiRepository apiRepository;

     @Autowired
     private AMQPRouteService amqpRoute;

     /**
      * Finds a {@link Api} by its ID.
      * 
      * @param 	id						The ID of the {@link Api}
      * @return							The {@link Api}
      * @throws	NotFoundException		Resource not found
      */
     public Api find(Long id) {
          
          Api api = apiRepository.findOne(id);      
          HeimdallException.checkThrow(isBlank(api), GLOBAL_RESOURCE_NOT_FOUND);
                              
          return api;
     }
     
     /**
      * Generates a paged list of the {@link Api}'s
      * 
      * @param 	apiDTO					{@link ApiDTO}
      * @param 	pageableDTO				{@link PageableDTO}
      * @return 						The paged {@link Api} list as a {@link ApiPage} object
      */
     public ApiPage list(ApiDTO apiDTO, PageableDTO pageableDTO) {

          Api api = GenericConverter.mapper(apiDTO, Api.class);
          
          Example<Api> example = Example.of(api, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<Api> page = apiRepository.findAll(example, pageable);
          
          ApiPage apiPage = new ApiPage(PageDTO.build(page));
          
          return apiPage;
     }

     /**
      * Generates a list of the {@link Api}'s
      * 
      * @param 	apiDTO					{@link ApiDTO}
      * @param 	pageableDTO				The pageable DTO
      * @return 						The list of {@link Api}'s
      */
     public List<Api> list(ApiDTO apiDTO) {
          
          Api api = GenericConverter.mapper(apiDTO, Api.class);
          
          Example<Api> example = Example.of(api, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          List<Api> apis = apiRepository.findAll(example);
          
          return apis;
     }
     
     /**
      * Saves a {@link Api}.
      * 
      * @param 	apiDTO					{@link ApiDTO}
      * @return							The saved {@link Api}
      * @throws	BadRequestException		The basepath defined exist
      * @throws     BadRequestException      Api basepath can not contain wild card
      * @throws     BadRequestException      Basepath can not be empty
      */
     public Api save(ApiDTO apiDTO) {
          
          Api validateApi = apiRepository.findByBasePath(apiDTO.getBasePath());
          HeimdallException.checkThrow(notBlank(validateApi), API_BASEPATH_EXIST);
          HeimdallException.checkThrow(validateBasepath(apiDTO), API_BASEPATH_MALFORMED);
          HeimdallException.checkThrow(isBlank(apiDTO.getBasePath()), API_BASEPATH_EMPTY);

          Api api = GenericConverter.mapperWithMapping(apiDTO, Api.class, new ApiMap());
          
          api = apiRepository.save(api);
          
          amqpRoute.dispatchRoutes();
          return api;
     }

     /**
      * Updates a {@link Api} by its ID.
      *  
      * @param 	id						The ID of the {@link Api}
      * @param 	apiDTO					{@link ApiDTO}
      * @return							The updated {@link Api}
      * @throws	NotFoundException		Resource not found
      * @throws	BadRequestException		The basepath defined exist
      * @throws     BadRequestException      Api basepath can not contain wild card
      * @throws     BadRequestException      Basepath can not be empty
      */
     public Api update(Long id, ApiDTO apiDTO) {

          Api api = apiRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(api), GLOBAL_RESOURCE_NOT_FOUND);
          
          Api validateApi = apiRepository.findByBasePath(apiDTO.getBasePath());
          HeimdallException.checkThrow(notBlank(validateApi) && validateApi.getId() != api.getId(), API_BASEPATH_EXIST);
          HeimdallException.checkThrow(validateBasepath(apiDTO), API_BASEPATH_MALFORMED);
          HeimdallException.checkThrow(isBlank(apiDTO.getBasePath()), API_BASEPATH_EMPTY);

          api = GenericConverter.mapperWithMapping(apiDTO, api, new ApiMap());
          api = apiRepository.save(api);
          
          amqpRoute.dispatchRoutes();
          
          return api;
     }
     
     /**
      * Deletes a {@link Api} by its ID.
      * 
      * @param 	id						The ID of the {@link Api}
      * @throws	NotFoundException		Resource not found
      */
     public void delete(Long id) {

          Api api = apiRepository.findOne(id);
          HeimdallException.checkThrow(isBlank(api), GLOBAL_RESOURCE_NOT_FOUND);
          
          apiRepository.delete(api);
          amqpRoute.dispatchRoutes();
     }

     /*
      * A Api basepath can not have any sort of wild card.
      */
     private static boolean validateBasepath(ApiDTO apiDTO) {
          List<String> basepath = Arrays.asList(
                    apiDTO.getBasePath()
                    .split("/")
                );
          
          return basepath.contains("*") || basepath.contains("**");
     }
}
