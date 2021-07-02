/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 *
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
package br.com.heimdall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import br.com.heimdall.core.environment.Property;
import br.com.heimdall.core.util.RabbitQueueUtils;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is the main Heimdall Gateway Application class. <br>
 * Heimdall Gateway is a SpringBoot based application that uses RabbitMQ as a message broker
 * This class starts the RabbitQueue then runs the Gateway Application as a SpringBoot application.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableConfigurationProperties({ Property.class })
@ComponentScan(basePackages = { "br.com.heimdall.gateway", "br.com.heimdall.core" })
@EntityScan("br.com.heimdall.core.entity")
@EnableJpaRepositories("br.com.heimdall.core.repository")
@EnableCaching
@EnableScheduling
public class GatewayApplication extends SpringBootServletInitializer  {
     
     public static void main(String[] args) {

          RabbitQueueUtils.init();
          SpringApplication.run(GatewayApplication.class, args);
     }

}