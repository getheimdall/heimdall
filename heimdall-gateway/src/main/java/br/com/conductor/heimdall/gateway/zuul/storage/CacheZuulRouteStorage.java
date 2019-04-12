
package br.com.conductor.heimdall.gateway.zuul.storage;

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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import com.google.common.collect.Sets;

import br.com.conductor.heimdall.core.repository.jdbc.ApiJDBCRepository;
import br.com.conductor.heimdall.core.repository.jdbc.OperationJDBCRepository;
import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.util.RouteSort;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents the place that will fill the ZuulRoutes
 * 
 * @author Marcos Filho
 *
 */
@Slf4j
public class CacheZuulRouteStorage implements ZuulRouteStorage {

	@Autowired
	private ApiJDBCRepository apiJDBCRepository;

	@Autowired
	private OperationJDBCRepository operationJDBCRepository;

	@Value("${info.app.profile}")
	private String profile;

	@Value("${heimdall.retryable}")
	private boolean retryable;

	@Override
	public List<ZuulRoute> findAll() {

		return init();
	}

	/**
	 * Gets a ordered List of {@link ZuulRoute}.
	 *
	 * @return A ordered List of {@link ZuulRoute}
	 */
	public List<ZuulRoute> init() {

		log.info("Initialize routes from profiles: " + profile);
		List<ZuulRoute> routes = new LinkedList<>();
		List<Long> apiIds = apiJDBCRepository.findAllIds();
		boolean production = Constants.PRODUCTION.equals(profile);

		String destination;

		if (production) {
			destination = "producao";
		} else {
			destination = "sandbox";
		}

		List<String> apiPathConcatWithOperationPaths = operationJDBCRepository.findOperationsFromAllApis(apiIds);

		if (Objects.nonNull(apiPathConcatWithOperationPaths) && !apiPathConcatWithOperationPaths.isEmpty()) {

			for (String completePath : apiPathConcatWithOperationPaths) {

				ZuulRoute route = new ZuulRoute(completePath, destination);
				route.setStripPrefix(false);
				route.setSensitiveHeaders(Sets.newConcurrentHashSet());
				route.setRetryable(retryable);
				routes.add(route);
			}
		}

		routes.sort(new RouteSort());
		return routes;
	}
}
