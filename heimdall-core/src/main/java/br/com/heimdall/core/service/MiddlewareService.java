
package br.com.heimdall.core.service;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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

import static br.com.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.heimdall.core.exception.ExceptionMessage.MIDDLEWARE_UNSUPPORTED_TYPE;
import static br.com.heimdall.core.exception.ExceptionMessage.ONLY_ONE_MIDDLEWARE_PER_VERSION_AND_API;

import java.util.*;
import java.util.stream.Collectors;

import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.entity.Middleware;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.heimdall.core.converter.GenericConverter;
import br.com.heimdall.core.dto.MiddlewareDTO;
import br.com.heimdall.core.dto.PageDTO;
import br.com.heimdall.core.dto.PageableDTO;
import br.com.heimdall.core.dto.page.MiddlewarePage;
import br.com.heimdall.core.enums.Status;
import br.com.heimdall.core.enums.TypeInterceptor;
import br.com.heimdall.core.environment.Property;
import br.com.heimdall.core.exception.ExceptionMessage;
import br.com.heimdall.core.exception.HeimdallException;
import br.com.heimdall.core.repository.ApiRepository;
import br.com.heimdall.core.repository.InterceptorRepository;
import br.com.heimdall.core.repository.MiddlewareRepository;
import br.com.heimdall.core.service.amqp.AMQPMiddlewareService;
import br.com.heimdall.core.util.Pageable;
import lombok.extern.slf4j.Slf4j;

/**
 * This class provides methods to create, read, update and delete the {@link Middleware} resource.
 *
 * @author Filipe Germano
 *
 */
@Service
@Slf4j
public class MiddlewareService {

     @Autowired
     private MiddlewareRepository middlewareRepository;

     @Autowired
     private ApiRepository apiRepository;

     @Autowired
     private InterceptorRepository interceptorRepository;

     @Autowired
     private Property property;

     @Value("${zuul.filter.root}")
     private String root;

     @Autowired
     private AMQPMiddlewareService amqpMiddlewareService;

     /**
      * Finds a {@link Middleware} by its Id and {@link Api} Id.
      *
      * @param 	apiId 					The {@link Api} Id
      * @param 	middlewareId 			The {@link Middleware} Id
      * @return  						The {@link Middleware} associated with the {@link Api}
      */
     @Transactional(readOnly = true)
     public Middleware find(Long apiId, Long middlewareId) {

          Middleware middleware = middlewareRepository.findByApiIdAndId(apiId, middlewareId);
          HeimdallException.checkThrow(middleware == null, GLOBAL_RESOURCE_NOT_FOUND);

          return middleware;
     }

     /**
      * Generates a paged list of the {@link Middleware} associated with a {@link Api}.
      *
      * @param 	apiId 					The ID of the {@link Api}
      * @param 	middlewareDTO 			The {@link MiddlewareDTO}
      * @param	pageableDTO 			The {@link PageableDTO}
      * @return  						A paged {@link Middleware} list as a {@link MiddlewarePage} object
      */
     @Transactional(readOnly = true)
     public MiddlewarePage list(Long apiId, MiddlewareDTO middlewareDTO, PageableDTO pageableDTO) {

          Example<Middleware> example = this.createExample(apiId, middlewareDTO);

          Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "creationDate"));

          Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit(), sort);
          Page<Middleware> page = middlewareRepository.findAll(example, pageable);


          return new MiddlewarePage(PageDTO.build(page));
     }

     /**
      * Generates a list of the {@link Middleware} associated with a {@link Api}.
      *
      * @param 	apiId 						The ID of the API
      * @param 	middlewareDTO 				The middleware DTO
      * @return 						 	The list of {@link Middleware}
      */
     @Transactional(readOnly = true)
     public List<Middleware> list(Long apiId, MiddlewareDTO middlewareDTO) {

          Example<Middleware> example = this.createExample(apiId, middlewareDTO);

          return middlewareRepository.findAll(example);
     }

     private Example<Middleware> createExample(Long apiId, MiddlewareDTO middlewareDTO) {
         Api api = apiRepository.findOne(apiId);
         HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

         Middleware middleware = GenericConverter.mapper(middlewareDTO, Middleware.class);
         Api apiFind = new Api();
         apiFind.setId(apiId);
         middleware.setApi(apiFind);

         return Example.of(middleware, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
     }

     /**
      * Save a new {@link Middleware} for a {@link Api}.
      *
      * @param 	apiId 					The {@link Api} Id
      * @param 	middlewareDTO 			The {@link MiddlewareDTO}
      * @param 	file 					The packaged {@link Middleware} file
      * @return 						The new {@link Middleware} created
      */
     @Transactional
     public Middleware save(Long apiId, MiddlewareDTO middlewareDTO, MultipartFile file) {

    	  List<Middleware> middlewares = updateMiddlewaresStatus(middlewareRepository.findByApiId(apiId));

    	  if (middlewares != null && !middlewares.isEmpty())
    		  middlewareRepository.save(middlewares);

          Api api = apiRepository.findOne(apiId);
          HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

          Middleware resData = middlewareRepository.findByApiIdAndVersion(apiId, middlewareDTO.getVersion());
          HeimdallException.checkThrow(resData != null &&
                  (Objects.equals(resData.getApi().getId(), api.getId())), ONLY_ONE_MIDDLEWARE_PER_VERSION_AND_API);

          String type = FilenameUtils.getExtension(file.getOriginalFilename());
          HeimdallException.checkThrow(!("jar".equalsIgnoreCase(type)), MIDDLEWARE_UNSUPPORTED_TYPE);

          Middleware middleware = GenericConverter.mapper(middlewareDTO, Middleware.class);
          middleware.setApi(api);
          middleware.setPath(root + "/api/" + apiId + "/middleware");
          middleware.setType(type);
          try {

               middleware.setFile(file.getBytes());
          } catch (Exception e) {

               log.error(e.getMessage(), e);
          }

          List<Interceptor> interceptors = interceptorRepository.findByTypeAndOperationResourceApiId(TypeInterceptor.MIDDLEWARE, middleware.getApi().getId());
          middleware.setInterceptors(interceptors);
          middleware = middlewareRepository.save(middleware);

          amqpMiddlewareService.dispatchCreateMiddlewares(middleware.getId());

          return middleware;
     }

     /**
      * Updates a middleware by Middleware ID and API ID.
      *
      * @param 	apiId 					The ID of the API
      * @param 	middlewareId 			The middleware ID
      * @param	middlewareDTO 			The middleware DTO
      * @return 						The middleware that was updated
      */
     @Transactional
     public Middleware update(Long apiId, Long middlewareId, MiddlewareDTO middlewareDTO) {

          Middleware middleware = middlewareRepository.findByApiIdAndId(apiId, middlewareId);
          HeimdallException.checkThrow(middleware == null, GLOBAL_RESOURCE_NOT_FOUND);

          Middleware resData = middlewareRepository.findByApiIdAndVersion(apiId, middlewareDTO.getVersion());
          HeimdallException.checkThrow(resData != null &&
                  Objects.equals(resData.getApi().getId(), middleware.getApi().getId()) &&
                  !Objects.equals(resData.getId(), middleware.getId()), ONLY_ONE_MIDDLEWARE_PER_VERSION_AND_API);

          middleware = GenericConverter.mapper(middlewareDTO, middleware);

          Boolean deleteDeprecated = property.getMiddlewares().getDeleteDeprecated();

          if (middleware.getStatus().equals(Status.DEPRECATED) && deleteDeprecated != null && deleteDeprecated){
               middleware.setFile(null);
          }


          middleware = middlewareRepository.save(middleware);

          amqpMiddlewareService.dispatchCreateMiddlewares(middlewareId);

          return middleware;
     }

     /**
      * Deletes a middleware by API ID and middleware ID
      *
      * @param 	apiId					The ID of the API
      * @param 	middlewareId			The middleware ID
      */
     @Transactional
     public void delete(Long apiId, Long middlewareId) {

          Middleware middleware = middlewareRepository.findByApiIdAndId(apiId, middlewareId);
          HeimdallException.checkThrow(middleware == null, GLOBAL_RESOURCE_NOT_FOUND);
          HeimdallException.checkThrow(middleware.getInterceptors() != null &&
                  !middleware.getInterceptors().isEmpty(), ExceptionMessage.MIDDLEWARE_CONTAINS_INTERCEPTORS);

          amqpMiddlewareService.dispatchRemoveMiddlewares(middleware.getPath());
          middlewareRepository.delete(middleware.getId());

     }

     /**
      * Deletes all Middlewares from a Api
      *
      * @param apiId Api with the Middlewares
      */
     @Transactional
     public void deleteAll(Long apiId) {
         List<Middleware> middlewares = middlewareRepository.findByApiId(apiId);
         middlewares.forEach(middleware -> this.delete(apiId, middleware.getId()));
     }

     /*
      * Updates the status of current middleware repository based on the Property settings.
      *
      * ACTIVE -> changes status to INACTIVE
      *
      * INACTIVE -> deprecated if number exceeds property allowInactive,
      * 			if deleteDeprecated is true, deletes the middleware file from database
      *
      * DEPRECATED -> do not change status
      */
	 private List<Middleware> updateMiddlewaresStatus(List<Middleware> list) {

		if (list != null && !list.isEmpty()) {
			Map<Status, List<Middleware>> middlewareMap = list.stream()
					.collect(Collectors.groupingBy(Middleware::getStatus));

			Integer allowInactive = property.getMiddlewares().getAllowInactive();
			Boolean deleteDeprecated = property.getMiddlewares().getDeleteDeprecated();

			if (allowInactive != null && allowInactive > 0) {

				List<Middleware> active = middlewareMap.get(Status.ACTIVE) != null ? middlewareMap.get(Status.ACTIVE) : Collections.emptyList();
				List<Middleware> inactive = new ArrayList<>(middlewareMap.get(Status.INACTIVE) != null ? middlewareMap.get(Status.INACTIVE) : Collections.emptyList());
				List<Middleware> deprecated = new ArrayList<>(middlewareMap.get(Status.DEPRECATED) != null ? middlewareMap.get(Status.DEPRECATED) : Collections.emptyList());

                active.forEach(m -> m.setStatus(Status.INACTIVE));

                inactive.addAll(active);
                inactive.sort((m1, m2) -> m2.getCreationDate().compareTo(m1.getCreationDate()));
                inactive.stream().skip(allowInactive).forEach(m -> {
                    m.setStatus(Status.DEPRECATED);
                    deprecated.add(m);
                });

                if (deleteDeprecated != null && deleteDeprecated) {
                    middlewareRepository.delete(deprecated);
                    list.removeAll(deprecated);
                }

			} else {
				middlewareMap.get(Status.ACTIVE).forEach(m -> m.setStatus(Status.INACTIVE));
			}
		}

		return list;
	}

}
