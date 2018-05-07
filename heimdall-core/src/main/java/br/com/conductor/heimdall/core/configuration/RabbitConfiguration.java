
package br.com.conductor.heimdall.core.configuration;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import br.com.conductor.heimdall.core.util.RabbitConstants;

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

     @Value("${heimdall.queue.interceptors}")
     private String queueInterceptors;

     @Value("${heimdall.queue.caches-clean}")
     private String queueCachesClean;

     @Value("${heimdall.queue.refresh-interceptors}")
     private String queueRefreshAllInterceptors;
     
     @Value("${heimdall.queue.routes}")
     private String queueRoutes;
     
     @Value("${heimdall.queue.interceptors-remove}")
     private String queueRemoveInterceptors;

     @Value("${heimdall.queue.middlewares}")
     private String queueMiddlewares;

     @Value("${heimdall.queue.remove-middlewares}")
     private String queueRemoveMiddlewares;

     @Bean
     public FanoutExchange exchangeFanoutRemoveInterceptors() {

          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_REMOVE_INTERCEPTORS, false, true);
     }

     @Bean
     public FanoutExchange exchangeFanoutAddInterceptors() {
          
          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_ADD_INTERCEPTORS, false, true);
     }

     @Bean
     public FanoutExchange exchangeFanoutRefreshAllInterceptors() {
          
          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_REFRESH_ALL_INTERCEPTORS, false, true);
     }

     @Bean
     public FanoutExchange exchangeFanoutCleanAllCaches() {
          
          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_CLEAN_ALL_CACHES, false, true);
     }
     
     @Bean
     public FanoutExchange exchangeFanoutRoutes() {

          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_ROUTES, false, true);
     }

     @Bean
     public FanoutExchange exchangeFanoutMiddlewares() {
          
          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_MIDDLEWARES, false, true);
     }

     @Bean
     public FanoutExchange exchangeFanoutRemoveMiddlewares() {
          
          return new FanoutExchange(RabbitConstants.EXCHANGE_FANOUT_HEIMDALL_REMOVE_MIDDLEWARES, false, true);
     }

     @Bean
     public Queue queueRemoveInterceptors() {
          
          return new Queue(queueRemoveInterceptors, false, false, true);
     }


     @Bean
     public Queue queueRoutes() {

          return new Queue(queueRoutes, false, false, true);
     }

     @Bean
     public Binding bindingRoutes() {

          return BindingBuilder.bind(queueRoutes()).to(exchangeFanoutRoutes());
     }

     @Bean
     public Queue queueInterceptors() {
          
          return new Queue(queueInterceptors, false, false, true);

     }

     @Bean
     public Queue queueCachesClean() {
          
          return new Queue(queueCachesClean, false, false, true);
          
     }

     @Bean
     public Queue queueRefreshAllInterceptors() {
          
          return new Queue(queueRefreshAllInterceptors, false, false, true);
          
     }

     @Bean
     public Queue queueMiddlewares() {
          
          return new Queue(queueMiddlewares, false, false, true);
          
     }

     @Bean
     public Queue queueRemoveMiddlewares() {
          
          return new Queue(queueRemoveMiddlewares, false, false, true);
          
     }
     
     @Bean
     public Binding bindingRemoveInterceptors() {
          
          return BindingBuilder.bind(queueRemoveInterceptors()).to(exchangeFanoutRemoveInterceptors());
     }


     @Bean
     public Binding bindingInterceptors() {

          return BindingBuilder.bind(queueInterceptors()).to(exchangeFanoutAddInterceptors());
     }

     @Bean
     public Binding bindingRefreshAllInterceptors() {
          
          return BindingBuilder.bind(queueRefreshAllInterceptors()).to(exchangeFanoutRefreshAllInterceptors());
     }

     @Bean
     public Binding bindingCachesClean() {
          
          return BindingBuilder.bind(queueCachesClean()).to(exchangeFanoutCleanAllCaches());
     }

     @Bean
     public Binding bindingMiddlewares() {
          
          return BindingBuilder.bind(queueMiddlewares()).to(exchangeFanoutMiddlewares());
     }

     @Bean
     public Binding bindingRemoveMiddlewares() {
          
          return BindingBuilder.bind(queueRemoveMiddlewares()).to(exchangeFanoutRemoveMiddlewares());
     }

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
