/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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
package br.com.conductor.heimdall.api.configuration;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

/**
 * Class provides the configuration for a RabbitMQ.
 *
 * @author Filipe Germano
 *
 */
@ConditionalOnProperty(name = "heimdall.excludeRabbit", matchIfMissing = true)
@EnableRabbit
@Configuration
public class RabbitConfiguration implements RabbitListenerConfigurer {

     @Bean
     public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {

          final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
          rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
          return rabbitTemplate;
     }

     @Bean
     public Jackson2JsonMessageConverter producerJackson2MessageConverter() {

          return new Jackson2JsonMessageConverter();
     }

     @Bean
     public MappingJackson2MessageConverter consumerJackson2MessageConverter() {

          return new MappingJackson2MessageConverter();
     }

     @Bean
     public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {

          DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
          factory.setMessageConverter(consumerJackson2MessageConverter());
          return factory;
     }

     @Override
     public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {

          registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
     }
     
}
