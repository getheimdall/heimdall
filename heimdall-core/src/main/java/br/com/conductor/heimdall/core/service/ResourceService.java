
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
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.ONLY_ONE_RESOURCE_PER_API;
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
import br.com.conductor.heimdall.core.converter.ResourceMap;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ResourceDTO;
import br.com.conductor.heimdall.core.dto.page.ResourcePage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.ResourceRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.Pageable;

/**
 * This class provides methos to create, read, update and delete a {@link Resource} resource.
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 *
 */
@Service
public class ResourceService {

     @Autowired
     private ResourceRepository resourceRepository;

     @Autowired
     private ApiRepository apiRepository;

     @Autowired
     private OperationService operationService;

     @Autowired
     private InterceptorService interceptorService;
     
     @Autowired
     private AMQPRouteService amqpRoute;


     /**
      * Finds a {@link Resource} by its Id and {@link Api} Id.
      * 
      * @param 	apiId						The {@link Api} Id
      * @param 	resourceId					The {@link Resource} Id
      * @return								The {@link Resource} found
      * @throws NotFoundException			Resource not found
      */
     @Transactional(readOnly = true)
     public Resource find(Long apiId, Long resourceId) {
          
          Resource resource = resourceRepository.findByApiIdAndId(apiId, resourceId);      
          HeimdallException.checkThrow(isBlank(resource), GLOBAL_RESOURCE_NOT_FOUND);
                              
          return resource;
     }
     
     /**
      * Generates a paged list of {@link Resource} from a request.
      * 
      * @param 	apiId						The {@link Api} Id
      * @param 	resourceDTO					The {@link ResourceDTO}
      * @param 	pageableDTO					The {@link PageableDTO}
      * @return								The paged {@link Resource} list as a {@link ResourcePage} object
      * @throws NotFoundException			Resource not found
      */
     public ResourcePage list(Long apiId, ResourceDTO resourceDTO, PageableDTO pageableDTO) {

          Api api = apiRepository.findOne(apiId);
          HeimdallException.checkThrow(isBlank(api), GLOBAL_RESOURCE_NOT_FOUND);
          
          Resource resource = GenericConverter.mapper(resourceDTO, Resource.class);
          resource.setApi(api);
          
          Example<Resource> example = Example.of(resource, ExampleMatcher.matching().withIgnorePaths("api.creationDate").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
          Page<Resource> page = resourceRepository.findAll(example, pageable);
          
          ResourcePage resourcePage = new ResourcePage(PageDTO.build(page));
          
          return resourcePage;
     }

     /**
      * Generates a list of {@link Resource} from a request.
      * 
      * @param 	apiId						The {@link Api} Id
      * @param 	resourceDTO					The {@link ResourceDTO}
      * @return								The List of {@link Resource}
      * @throws NotFoundException			Resource not found
      */
     public List<Resource> list(Long apiId, ResourceDTO resourceDTO) {
          
          Api api = apiRepository.findOne(apiId);
          HeimdallException.checkThrow(isBlank(api), GLOBAL_RESOURCE_NOT_FOUND);
          
          Resource resource = GenericConverter.mapper(resourceDTO, Resource.class);
          resource.setApi(api);
          
          Example<Resource> example = Example.of(resource, ExampleMatcher.matching().withIgnorePaths("api.creationDate").withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
          
          List<Resource> resources = resourceRepository.findAll(example);
          
          return resources;
     }
     
     /**
      * Saves a {@link Resource} to the repository.
      * 
      * @param 	apiId						The {@link Api} Id
      * @param 	resourceDTO					The {@link ResourceDTO}
      * @return								The saved {@link Resource}
      * @throws NotFoundException			Resource not found
      * @throws BadRequestException			Only one resource per api
      */
     public Resource save(Long apiId, ResourceDTO resourceDTO) {

          Api api = apiRepository.findOne(apiId);
          HeimdallException.checkThrow(isBlank(api), GLOBAL_RESOURCE_NOT_FOUND);
                    
          Resource resData = resourceRepository.findByApiIdAndName(apiId, resourceDTO.getName());
          HeimdallException.checkThrow(notBlank(resData) && (resData.getApi().getId() == api.getId()), ONLY_ONE_RESOURCE_PER_API);
          
          Resource resource = GenericConverter.mapperWithMapping(resourceDTO, Resource.class, new ResourceMap());
          resource.setApi(api);
          
          resource = resourceRepository.save(resource);
          
          amqpRoute.dispatchRoutes();
          
          return resource;
     }

     /**
      * Updates a {@link Resource} by its Id and {@link Api} Id
      * 
      * @param 	apiId						The {@link Api} Id
      * @param 	resourceId					The {@link Resource} Id
      * @param 	resourceDTO					The {@link ResourceDTO}
      * @return								The updated {@link Resource}
      * @throws NotFoundException			Resource not found
      * @throws BadRequestException			Only one resource per api
      */
     public Resource update(Long apiId, Long resourceId, ResourceDTO resourceDTO) {

          Resource resource = resourceRepository.findByApiIdAndId(apiId, resourceId);
          HeimdallException.checkThrow(isBlank(resource), GLOBAL_RESOURCE_NOT_FOUND);
          
          Resource resData = resourceRepository.findByApiIdAndName(apiId, resourceDTO.getName());
          HeimdallException.checkThrow(notBlank(resData) && (resData.getApi().getId() == resource.getApi().getId()) && (resData.getId() != resource.getId()), ONLY_ONE_RESOURCE_PER_API);
          
          resource = GenericConverter.mapperWithMapping(resourceDTO, resource, new ResourceMap());
          
          resource = resourceRepository.save(resource);
          
          amqpRoute.dispatchRoutes();
          
          return resource;
     }
     
     /**
      * Deletes a {@link Resource} by its Id.
      * 
      * @param 	apiId						The {@link Api} Id
      * @param 	resourceId					The {@link Resource} Id
      * @throws NotFoundException			Resource not found
      */
     public void delete(Long apiId, Long resourceId) {

          Resource resource = resourceRepository.findByApiIdAndId(apiId, resourceId);
          HeimdallException.checkThrow(isBlank(resource), GLOBAL_RESOURCE_NOT_FOUND);

          // Deletes all operations attached to the Resource
          operationService.deleteAllfromResource(apiId, resourceId);

          // Deletes all interceptors attached to the Resource
          interceptorService.deleteAllfromResource(resourceId);
          
          resourceRepository.delete(resource.getId());
          
          amqpRoute.dispatchRoutes();
     }

     /**
      * Deletes all Resources from a Api
      *
      * @param apiId Api with the Resources
      */
     public void deleteAllFromApi(Long apiId) {
          List<Resource> resources = resourceRepository.findByApiId(apiId);
          resources.forEach(resource -> this.delete(apiId, resource.getId()));
     }
}
