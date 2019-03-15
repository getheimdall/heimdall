
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

import br.com.conductor.heimdall.core.service.CacheService;
import br.com.conductor.heimdall.core.util.RabbitConstants;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.extern.slf4j.Slf4j;

/**
 * Keeps track of the Rabbit Cache to clean it when necessary.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
@Component
public class CacheListener {
     
     @Autowired
     private RabbitTemplate rabbitTemplate;
     
     @Autowired
     private CacheService cacheService;

     /**
      * Cleans the Rabbit cache with specific message.
      * 
      * @param message	{@link Message}
      */
     @RabbitListener( queues = RabbitConstants.LISTENER_HEIMDALL_CLEAN_CACHE)
     public void cleanCaches(final Message message) {
          
          String key = (String) rabbitTemplate.getMessageConverter().fromMessage(message);
          
          
          if (Objeto.notBlank(key)) {
               
               if (key.contains(";")) {
                    
                    String[] split = key.split(";");
                    log.info("Clean cache with key: {} and id: {} ", split[0], split[1]);
                    cacheService.clean(split[0], split[1]);
               } else {
                    
                    log.info("Clean cache with key: {}", key);
                    cacheService.clean(key);
               }
          } else {
               
               log.info("Clean all caches");
               cacheService.clean();
          }
          
     }

     /**
      * Cleans the Rabbit cache with specific message.
      *
      * @param message	{@link Message}
      */
     @RabbitListener( queues = RabbitConstants.LISTENER_HEIMDALL_CLEAN_INTERCEPTORS_CACHE)
     public void cleanInterceptorsCache(final Message message) {

          log.info("Clean all caches from Cache interceptors");
          cacheService.cleanInterceptorsCache();
     }

}
