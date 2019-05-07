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
package br.com.conductor.heimdall.gateway.configuration;

import br.com.conductor.heimdall.core.environment.Property;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Represents the configuration used by mongo.
 * @author marcos.filho
 *
 */
@Configuration
public class MongoConfiguration {

	@Autowired
	private Property property;

	@Bean
	public MongoClient createMongoClient() {

		MongoClient client;
		if (property.getMongo().getUrl() != null) {

			MongoClientURI uri = new MongoClientURI(property.getMongo().getUrl());
			client = new MongoClient(uri);
		} else {
			ServerAddress address = new ServerAddress(property.getMongo().getServerName(), property.getMongo().getPort().intValue());
			MongoCredential mongoCredential = MongoCredential.createCredential(property.getMongo().getUsername(), property.getMongo().getUsername(), property.getMongo().getPassword().toCharArray());
			MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
			client = new MongoClient(address, mongoCredential, mongoClientOptions);
		}

		return client;
	}
}
