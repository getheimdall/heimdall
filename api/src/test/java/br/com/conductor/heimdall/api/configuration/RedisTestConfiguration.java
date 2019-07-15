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

import br.com.conductor.heimdall.core.publisher.MessagePublisher;
import br.com.conductor.heimdall.core.publisher.RedisMessagePublisher;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <h1>RedisTestConfiguration</h1><br/>
 * <p>
 * Class responsible for the Redis configuration.
 *
 * @author Marcos Filho
 * @see <a href="https://redis.io/">https://redis.io/</a>
 */
@TestConfiguration
public class RedisTestConfiguration {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

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
    public MessagePublisher redisPublisher() {
        return Mockito.mock(RedisMessagePublisher.class);
    }

    /**
     * Returns a configured {@link RedisTemplate}.
     *
     * @return {@link RedisTemplate} Object, Object
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());

        return redisTemplate;

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
