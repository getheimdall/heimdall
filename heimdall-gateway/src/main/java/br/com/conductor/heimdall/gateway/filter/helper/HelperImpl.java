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
package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.util.BeanManager;
import br.com.conductor.heimdall.gateway.configuration.TimeoutCounter;
import br.com.conductor.heimdall.gateway.failsafe.CircuitBreakerManager;
import br.com.conductor.heimdall.gateway.filter.helper.http.HeimdallResponseErrorHandler;
import br.com.conductor.heimdall.middleware.enums.DBType;
import br.com.conductor.heimdall.middleware.spec.*;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of the {@link Helper} interface.
 *
 * @author Filipe Germano
 * @author marcos.filho
 */
public class HelperImpl implements Helper {
	
	@Autowired(required = false)
	private MongoClient mongoClient;
	
	private ThreadLocal<byte[]> buffers;

	private boolean enableHandler;

	@Autowired
	private Property property;
	
	@Autowired
	private ZuulProperties zuulProperty;

	@Autowired
	private CircuitBreakerManager circuitBreakerManager;
	
	private RestTemplate restTemplate;

	public HelperImpl() {
		enableHandler = false;
		buffers = ThreadLocal.withInitial(() -> new byte[8192]);
	}

	@Override
	public ApiResponse apiResponse() {

		ApiResponse apiResponse = new ApiResponseImpl();
		return apiResponse;
	}

	@Override
	public Call call() {

		return new CallImpl(buffers);
	}

	@Override
	public DB db(String databaseName) {

		return db(databaseName, DBType.MONGODB);
	}

	private DB db(String databaseName, DBType type) {

		switch (type) {
		default:
			return new DBMongoImpl(databaseName, mongoClient);
		}
	}

	@Override
	public DBMongo dbMongo(String databaseName) {

		return (DBMongo) db(databaseName, DBType.MONGODB);
	}

	@Override
	public Http http() {
		TimeoutCounter timeoutCounter = (TimeoutCounter) BeanManager.getBean(TimeoutCounter.class);
		return new HttpImpl(rest(), circuitBreakerManager, property.getFailsafe().isEnabled(), timeoutCounter);
	}
	
	private RestTemplate rest() {
        if (this.restTemplate == null) {
            this.restTemplate = new RestTemplate(httpClientRequestFactory());
        }

        if (enableHandler) {
            this.restTemplate.setErrorHandler(new HeimdallResponseErrorHandler());
        }
        return this.restTemplate;
    }
	
	private HttpComponentsClientHttpRequestFactory httpClientRequestFactory() {
		TimeoutCounter timeoutCounter = (TimeoutCounter) BeanManager.getBean(TimeoutCounter.class);
		HttpComponentsClientHttpRequestFactory httpClient = new HttpComponentsClientHttpRequestFactory();
		httpClient.setConnectTimeout(zuulProperty.getHost().getConnectTimeoutMillis());
		httpClient.setReadTimeout((int) timeoutCounter.getCounter());
		return httpClient;
	}

	@Override
	public Json json() {

		Json json = new JsonImpl();
		return json;
	}

	@Override
	public Xml xml() {

		return new XmlImpl();
	}

	@Override
	public void httpHandler(boolean useHandler) {
		this.enableHandler = useHandler;
	}

}
