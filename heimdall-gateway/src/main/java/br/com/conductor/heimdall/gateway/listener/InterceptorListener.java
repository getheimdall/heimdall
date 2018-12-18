
package br.com.conductor.heimdall.gateway.listener;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.repository.InterceptorRepository;
import br.com.conductor.heimdall.core.util.RabbitConstants;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener that controls the {@link Interceptor} repository.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
@Component
public class InterceptorListener {
     
     @Autowired
     private RabbitTemplate rabbitTemplate;
     
     @Autowired
     private InterceptorFileService interceptorFileService;

     @Autowired
     private InterceptorRepository interceptorRepository;

     @Autowired
     private StartServer startServer;

     /**
      * Updates the {@link Interceptor} repository.
      * 
      * @param message {@link Message}
      */
     @RabbitListener(queues = RabbitConstants.LISTENER_HEIMDALL_INTERCEPTORS)
     public void updateInterceptors(final Message message) {

          Long interceptorId = (Long) rabbitTemplate.getMessageConverter().fromMessage(message);


          Interceptor interceptor = interceptorRepository.findOne(interceptorId);
          if (Objeto.notBlank(interceptor)) {
               
               log.info("Updating/Creating Interceptor id: " + interceptorId);
               interceptorFileService.createFileInterceptor(interceptorId);
          } else {
               
               log.info("It was not possible Updating/Creating Interceptor id: " + interceptorId);
          }

     }

     /**
      * Refreshs all {@link Interceptor}.
      * 
      * @param message {@link Message}
      */
     @RabbitListener(queues = RabbitConstants.LISTENER_HEIMDALL_REFRESH_INTERCEPTORS)
     public void refreshAllInterceptors(final Message message) {

          log.info("Refresh all Interceptors");

          try {

               startServer.initApplication();
          } catch (Exception e) {

               log.error(e.getMessage(), e);
          }

     }
     
     /**
      * Removes a {@link Interceptor}.
      * 
      * @param message {@link Message}
      */
     @RabbitListener(queues = RabbitConstants.LISTENER_HEIMDALL_REMOVE_INTERCEPTORS)
     public void removeInterceptor(final Message message) {
          
          InterceptorFileDTO interceptor = (InterceptorFileDTO) rabbitTemplate.getMessageConverter().fromMessage(message);
          log.info("Removing Interceptor id: " + interceptor.getId());
          
          interceptorFileService.removeFileInterceptor(interceptor);
     }

}
