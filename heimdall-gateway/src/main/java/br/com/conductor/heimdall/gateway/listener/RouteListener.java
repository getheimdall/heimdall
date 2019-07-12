/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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
package br.com.conductor.heimdall.gateway.listener;

import br.com.conductor.heimdall.core.service.CacheService;
import br.com.conductor.heimdall.core.util.RabbitConstants;
import br.com.conductor.heimdall.gateway.configuration.HeimdallHandlerMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.stereotype.Component;

/**
 * Listener that controls the Routes.
 *
 * @author Marcos Filho
 *
 */
@Slf4j
@Component
public class RouteListener {

	@Autowired
	private CacheService cacheService;

	@Autowired
	private HeimdallHandlerMapping heimdallHandlerMapping;

	@Autowired
	private StartServer startServer;

	/**
	 * Updates the {@link ZuulRoute} repository.
	 * 
	 * @param message {@link Message}
	 */
	@RabbitListener(queues = RabbitConstants.LISTENER_HEIMDAL_ROUTES)
	public void updateZuulRoutes(final Message message) {
		try {
			log.info("Updating Zuul Routes");
			cacheService.clean();
			heimdallHandlerMapping.setDirty(false);
			startServer.initApplication();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
