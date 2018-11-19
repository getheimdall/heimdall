
package br.com.conductor.heimdall.api;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import br.com.conductor.heimdall.api.environment.JwtProperty;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.util.RabbitQueueUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Since Heimdall is a SpringBoot application, this is a {@link SpringBootServletInitializer}.
 *
 * @author Marcos Filho
 * @see <a href="https://projects.spring.io/spring-boot/">https://projects.spring.io/spring-boot/</a>
 * 
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableConfigurationProperties({ Property.class, JwtProperty.class})
@ComponentScan(basePackages = { "br.com.conductor.heimdall.api", "br.com.conductor.heimdall.core" })
@EntityScan({"br.com.conductor.heimdall.core.entity", "br.com.conductor.heimdall.api.entity"})
@EnableJpaRepositories({"br.com.conductor.heimdall.core.repository", "br.com.conductor.heimdall.api.repository"})
public class ApiApplication extends SpringBootServletInitializer  {

     public static void main(String[] args) {

          RabbitQueueUtils.init();
          SpringApplication.run(ApiApplication.class, args);
     }
     
}
