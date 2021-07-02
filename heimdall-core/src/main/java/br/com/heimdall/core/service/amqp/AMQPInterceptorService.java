
package br.com.heimdall.core.service.amqp;

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

import br.com.heimdall.core.dto.InterceptorFileDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.heimdall.core.entity.Interceptor;
import br.com.heimdall.core.util.RabbitConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * This class controls a {@link Interceptor} cache service.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@Service
@Slf4j
public class AMQPInterceptorService {

     @Autowired
     private RabbitTemplate rabbitTemplate;

     /**
      * Dispatch a message to update/create a interceptors
      * 
      * @param id
      */
     public void dispatchInterceptor(Long id) {
          
          rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_ADD_INTERCEPTORS, "", id);
          log.debug("Dispatch Interceptor");
     }

     /**
      * Dispatch a message to refresh all interceptors
      * 
      */
     public void dispatchRefreshAllInterceptors() {
          
          rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_REFRESH_ALL_INTERCEPTORS, "", "");
     }
     
     /**
      * Dispatch a message to remove a {@link Interceptor}
      * 
      * @param interceptor			The {@link InterceptorFileDTO}
      */
     public void dispatchRemoveInterceptors(InterceptorFileDTO interceptor) {
          rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_REMOVE_INTERCEPTORS, "", interceptor);
     }

}
