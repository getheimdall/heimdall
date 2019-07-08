/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.api.configuration;

import br.com.conductor.heimdall.core.entity.RateLimit;
import br.com.conductor.heimdall.core.environment.Property;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * <h1>RedisTestConfiguration</h1><br/>
 * 
 * Class responsible for the Redis configuration.
 *
 * @author Marcos Filho
 * @see <a href="https://redis.io/">https://redis.io/</a>
 */
@Configuration
@Profile("test")
public class RedisTestConfiguration {
     
     @Autowired
     Property property;
     
     /**
      * Creates a new {@link JedisConnectionFactory}.
      * 
      * @return {@link JedisConnectionFactory}
      */
     @Bean
     public JedisConnectionFactory jedisConnectionFactory() {

          return Mockito.mock(JedisConnectionFactory.class);
          
     }
     
     /**
      * Returns a configured {@link JedisPoolConfig}.
      * 
      * @return {@link JedisPoolConfig}
      */
     public JedisPoolConfig jediPoolConfig() {
          
          return Mockito.mock(JedisPoolConfig.class);
          
     }
     
     /**
      * Returns a configured {@link RedisTemplate}.
      * 
      * @return {@link RedisTemplate} Object, Object
      */
     @Bean
     public RedisTemplate<Object, Object> redisTemplateObject() {

          return Mockito.mock(RedisTemplate.class);
          
     }
     
     /**
      * Returns a configured {@link RedisTemplate}.
      * 
      * @return {@link RedisTemplate} String, {@link RateLimit}
      */
     @Bean
     public RedisTemplate<String, RateLimit> redisTemplateRate() {
          
          return Mockito.mock(RedisTemplate.class);
          
     }
     
     /**
      * Returns a configured {@link CacheManager}.
      * 
      * @return {@link CacheManager}
      */
     @Bean
     public CacheManager cacheManager() {
          
          return Mockito.mock(RedisCacheManager.class);
          
     }
     
     /**
      * Returns a configured {@link RedissonClient}.
      * 
      * @return {@link RedissonClient}
      */
     @Bean
     public RedissonClient redissonClient() {

          return Mockito.mock(RedissonClient.class);
          
     }
}
