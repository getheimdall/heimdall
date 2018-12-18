
package br.com.conductor.heimdall.core.service.amqp;

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

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.core.entity.Middleware;
import br.com.conductor.heimdall.core.util.RabbitConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * This class controls a {@link Middleware} cache service.
 *
 * @author Filipe Germano
 *
 */
@Service
@Slf4j
public class AMQPMiddlewareService {

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * Dispatch a message to refresh middlewares by id.
	 * 
	 * @param idMiddleware The {@link Middleware} Id
	 */
	public void dispatchCreateMiddlewares(Long idMiddleware) {

		log.info("Dispatching to create/update the middleware: {}", idMiddleware);
		rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_MIDDLEWARES, "", idMiddleware);
	}

	/**
	 * Dispatch a message to remove middlewares
	 * 
	 * @param path The path to the Middleware
	 */
	public void dispatchRemoveMiddlewares(String path) {
		log.info("Dispatching to remove middlewares from path: {}", path);
		rabbitTemplate.convertAndSend(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_REMOVE_MIDDLEWARES, "", path);
	}
}
