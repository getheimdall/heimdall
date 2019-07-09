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
package br.com.conductor.heimdall.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.conductor.heimdall.gateway.filter.helper.HelperImpl;
import br.com.conductor.heimdall.middleware.spec.Helper;

/**
 *
 * @author marcos.filho
 *
 */
@Configuration
public class HelperConfiguration {

     @Bean
     public Helper helper() {

          return new HelperImpl();
     }

     @Bean
     @RequestScope
     public TimeoutCounter timeoutCounter() {

          return new TimeoutCounter();
     }

}
