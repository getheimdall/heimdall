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

import br.com.conductor.heimdall.core.entity.RateLimit;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.util.ConstantsCache;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Class responsible for configuring the Redis.
 *
 * @author Marcos Filho
 *
 */
@Configuration
public class RedisConfiguration {
     
     @Autowired
     Property property;
     
     /**
      * Configures and returns a {@link JedisConnectionFactory}.
      * 
      * @return {@link JedisConnectionFactory}
      */
     @Bean
     public JedisConnectionFactory jedisConnectionFactory() {
          
          JedisConnectionFactory factory = new JedisConnectionFactory();
          
          factory.setHostName(property.getRedis().getHost());
          factory.setPort(property.getRedis().getPort());
          factory.setUsePool(true);
          factory.setPoolConfig(jediPoolConfig());
          return factory;
     }
     
     /**
      * Configures and returns a {@link JedisPoolConfig}.
      * 
      * @return {@link JedisPoolConfig}
      */
     public JedisPoolConfig jediPoolConfig() {
          final JedisPoolConfig poolConfig = new JedisPoolConfig();
          poolConfig.setMaxTotal(property.getRedis().getMaxTotal());
          poolConfig.setMaxIdle(property.getRedis().getMaxIdle());
          poolConfig.setMinIdle(property.getRedis().getMinIdle());
          poolConfig.setTestOnBorrow(property.getRedis().isTestOnBorrow());
          poolConfig.setTestOnReturn(property.getRedis().isTestOnReturn());
          poolConfig.setTestWhileIdle(property.getRedis().isTestWhileIdle());
          poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(property.getRedis().getMinEvictableIdleTimeSeconds()).toMillis());
          poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(property.getRedis().getTimeBetweenEvictionRunsSeconds()).toMillis());
          poolConfig.setNumTestsPerEvictionRun(property.getRedis().getNumTestsPerEvictionRun());
          poolConfig.setBlockWhenExhausted(property.getRedis().isBlockWhenExhausted());
          return poolConfig;
     }
     
     /**
      * Configures and returns {@link RedisTemplate} with Object and Object.
      * 
      * @return {@link RedisTemplate}
      */
     @Bean
     public RedisTemplate<Object, Object> redisTemplateObject() {

          RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
          redisTemplate.setConnectionFactory(jedisConnectionFactory());
          redisTemplate.setKeySerializer(new StringRedisSerializer());
          redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
          redisTemplate.setHashKeySerializer(new StringRedisSerializer());
          redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
          return redisTemplate;
     }
     
     /**
      * Configures and returns a {@link RedisTemplate} with String and {@link RateLimit}
      * 
      * @return {@link RedisTemplate}
      */
     @Bean
     public RedisTemplate<String, RateLimit> redisTemplateRate() {
          
          RedisTemplate<String, RateLimit> redisTemplate = new RedisTemplate<>();
          redisTemplate.setConnectionFactory(jedisConnectionFactory());
          redisTemplate.setKeySerializer(new StringRedisSerializer());
          redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
          redisTemplate.setHashKeySerializer(new StringRedisSerializer());
          redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
          return redisTemplate;
     }
     
     /**
      * Configures and returns a {@link CacheManager}.
      * 
      * @return {@link CacheManager}
      */
     @Bean
     public CacheManager cacheManager() {
          RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplateObject());
          redisCacheManager.setUsePrefix(true);
          
          return redisCacheManager;
     }
     
     /**
      * Configures and returns a {@link RedissonClient}.
      * 
      * @return {@link RedissonClient}
      */
     @Bean(autowire = Autowire.BY_NAME)
     public RedissonClient redissonClientRateLimitInterceptor() {

          return createConnection(ConstantsCache.RATE_LIMIT_DATABASE);
     }

     @Bean(autowire = Autowire.BY_NAME)
     public RedissonClient redissonClientCacheInterceptor() {

          return createConnection(ConstantsCache.CACHE_INTERCEPTOR_DATABASE);
     }

     private RedissonClient createConnection(int database) {

          Config config = new Config();
          config.useSingleServer()
                  .setAddress(property.getRedis().getHost() + ":" + property.getRedis().getPort())
                  .setConnectionPoolSize(property.getRedis().getConnectionPoolSize())
                  .setDatabase(database);

          return Redisson.create(config);
     }
}
