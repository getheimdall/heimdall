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
package br.com.conductor.heimdall.core.service.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.core.util.RabbitConstants;

/**
 * This class controls a Advanced Message Queuing Protocol (AMQP) cache service.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@Service
public class AMQPCacheService {

     @Autowired
     private RabbitTemplate rabbitTemplate;     

     /**
      * Dispatch a message to clean cache by key
      * 
      * @param key		The cache key
      */
     public void dispatchClean(String key) {
          
          rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_CLEAN_ALL_CACHES, "", key);
     }

     /**
      * Dispatch a message to clean cache by key and id
      * 
      * @param key		The cache key
      * @param id		The cache id
      */
     public void dispatchClean(String key, String id) {
          
          rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_CLEAN_ALL_CACHES, "", key + ";" + id);
     }

     /**
      * Dispatch a message to clean all caches
      * 
      */
     public void dispatchClean() {
          
          rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_CLEAN_ALL_CACHES, "", "");
     }

}
