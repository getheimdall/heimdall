
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

import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.repository.MiddlewareRepository;
import br.com.conductor.heimdall.core.util.RabbitConstants;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.extern.slf4j.Slf4j;

/**
 * Listener that controls the {@link Middleware} repository.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
@Component
public class MiddlewareListener {

     @Autowired
     private RabbitTemplate rabbitTemplate;

     @Autowired
     private MiddlewareRepository middlewareRepository;

     @Autowired
     private StartServer startServer;

     /**
      * Updates the {@link Middleware} repository.
      * 
      * @param message {@link Message}
      */
     @RabbitListener(queues = RabbitConstants.LISTENER_HEIMDALL_MIDDLEWARES)
     public void updateMiddlewares(final Message message) {
          
          try {
               Long middlewareId = (Long) rabbitTemplate.getMessageConverter().fromMessage(message);
     
               Middleware middleware = middlewareRepository.findOne(middlewareId);
               if (Objeto.notBlank(middleware)) {
                    
                    log.info("Updating/Creating middleware id: " + middlewareId);
                    startServer.addApiDirectoryToPath(middleware.getApi());
                    startServer.createMiddlewaresInterceptor(middlewareId);
                    startServer.loadMiddlewareFiles(middlewareId);
               } else {
                    
                    log.info("It was not possible Updating/Creating middleware id: " + middlewareId);
               }
          } catch (Exception e) {
               log.error(e.getMessage(), e);
          }
          
     }

     /**
      * Removes a {@link Middleware} from repository.
      * 
      * @param message {@link Message}
      */
     @RabbitListener(queues = RabbitConstants.LISTENER_HEIMDALL_REMOVE_MIDDLEWARES)
     public void removeMiddlewares(final Message message) {
          
          try {
               String path = (String) rabbitTemplate.getMessageConverter().fromMessage(message);
               
               if (Objeto.notBlank(path)) {
                    
                    log.info("Remove Middleware in: " + path);
                    startServer.removeMiddlewareFiles(path);
               } else {
                    
                    log.info("It was not possible Remove middleware in: " + path);
               }
          } catch (Exception e) {
               log.error(e.getMessage(), e);
          }
          
     }
}
