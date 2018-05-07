
package br.com.conductor.heimdall.api.configuration;

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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configures the Swagger.
 *
 * @author Filipe Germano
 * @see <a href="https://swagger.io/">https://swagger.io/</a>
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	/**
	 * Returns a {@link Docket} with the Heimdall information.
	 * 
	 * @return {@link Docket}
	 */
     @Bean
     public Docket swaggerSpringfoxDocket() {

          Docket docket = new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("br.com.conductor.heimdall.api.resource"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(apiInfo());

          return docket;
     }

     /*
      * Creates a ApiInfo with the Heimdall information.
      */
     private ApiInfo apiInfo() {

          ApiInfo apiInfo = new ApiInfoBuilder()
                    .title("Heimdall API Gateway")
                    .description("API Gateway for managing APIs")
                    .version("1")
                    .build();

          return apiInfo;
     }

}
