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
import br.com.conductor.heimdall.core.util.ConstantsCache;
import br.com.conductor.heimdall.core.util.RedisConstants;
import br.com.conductor.heimdall.gateway.listener.AddInterceptorsListener;
import br.com.conductor.heimdall.gateway.listener.RefreshInterceptorsListener;
import br.com.conductor.heimdall.gateway.listener.RefreshRoutesListener;
import br.com.conductor.heimdall.gateway.listener.RemoveInterceptorsListener;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Class responsible for configuring the Redis.
 *
 * @author Marcos Filho
 */
@Configuration
public class RedisConfiguration {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    private final AddInterceptorsListener addInterceptorsListener;

    private final RefreshInterceptorsListener refreshInterceptorsListener;

    private final RemoveInterceptorsListener removeInterceptorsListener;

    private final RefreshRoutesListener refreshRoutesListener;

    public RedisConfiguration(@Lazy AddInterceptorsListener addInterceptorsListener,
                              @Lazy RefreshInterceptorsListener refreshInterceptorsListener,
                              @Lazy RemoveInterceptorsListener removeInterceptorsListener,
                              @Lazy RefreshRoutesListener refreshRoutesListener) {
        this.addInterceptorsListener = addInterceptorsListener;
        this.refreshInterceptorsListener = refreshInterceptorsListener;
        this.removeInterceptorsListener = removeInterceptorsListener;
        this.refreshRoutesListener = refreshRoutesListener;
    }

    @Bean
    public MessageListenerAdapter addInterceptor() {
        return new MessageListenerAdapter(addInterceptorsListener);
    }

    @Bean
    public MessageListenerAdapter refreshInterceptor() {
        return new MessageListenerAdapter(refreshInterceptorsListener);
    }

    @Bean
    public MessageListenerAdapter removeInterceptor() {
        return new MessageListenerAdapter(removeInterceptorsListener);
    }

    @Bean
    public MessageListenerAdapter refreshRoutes() {
        return new MessageListenerAdapter(refreshRoutesListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(addInterceptor(), new ChannelTopic(RedisConstants.INTERCEPTORS_ADD));
        container.addMessageListener(refreshInterceptor(), new ChannelTopic(RedisConstants.INTERCEPTORS_REFRESH));
        container.addMessageListener(removeInterceptor(), new ChannelTopic(RedisConstants.INTERCEPTORS_REMOVE));
        container.addMessageListener(refreshRoutes(), new ChannelTopic(RedisConstants.ROUTES_REFRESH));

        return container;
    }

    /**
     * Creates a new {@link JedisConnectionFactory}.
     *
     * @return {@link JedisConnectionFactory}
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration
                = new RedisStandaloneConfiguration(redisHost, redisPort);

        return new JedisConnectionFactory(redisStandaloneConfiguration);

    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
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
     * Configures and returns a {@link RedissonClient}.
     *
     * @return {@link RedissonClient}
     */
    @Bean
    public RedissonClient redissonClientRateLimitInterceptor() {

        return createConnection(ConstantsCache.RATE_LIMIT_DATABASE);
    }

    @Bean
    public RedissonClient redissonClientCacheInterceptor() {

        return createConnection(ConstantsCache.CACHE_INTERCEPTOR_DATABASE);
    }

    private RedissonClient createConnection(int database) {

        Config config = new Config();
        config.useSingleServer()
                .setAddress(redisHost + ":" + redisPort)
                .setDatabase(database);

        return Redisson.create(config);
    }
}
