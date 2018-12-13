package br.com.conductor.heimdall.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import br.com.conductor.heimdall.core.environment.Property;
import br.com.twsoftware.alfred.object.Objeto;

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
	@ConditionalOnProperty(name="heimdall.mongo.enabled", havingValue="true")
	public MongoClient createMongoClient() {

		MongoClient client;
		if (Objeto.notBlank(property.getMongo().getUrl())) {

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
