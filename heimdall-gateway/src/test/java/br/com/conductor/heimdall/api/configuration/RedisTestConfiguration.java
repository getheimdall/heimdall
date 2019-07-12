
package br.com.conductor.heimdall.api.configuration;

import br.com.conductor.heimdall.core.entity.RateLimit;
import br.com.conductor.heimdall.core.environment.Property;
import org.mockito.Mockito;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
     * Returns a configured {@link RedisTemplate}.
     *
     * @return {@link RedisTemplate} Object, Object
     */
    @Bean
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> redisTemplate() {

        return Mockito.mock(RedisTemplate.class);

    }

    /**
     * Returns a configured {@link RedisTemplate}.
     *
     * @return {@link RedisTemplate} String, {@link RateLimit}
     */
    @Bean
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, RateLimit> redisTemplateRate() {

        return Mockito.mock(RedisTemplate.class);

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
