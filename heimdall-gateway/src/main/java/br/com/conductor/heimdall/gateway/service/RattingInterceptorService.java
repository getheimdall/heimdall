/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.entity.RateLimit;
import br.com.conductor.heimdall.core.repository.RateLimitRepository;
import br.com.conductor.heimdall.core.util.BeanManager;
import com.netflix.zuul.context.RequestContext;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Rate limit interceptor provides a limit to the number of requests
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class RattingInterceptorService {

    /**
     * Limits the number of requests to a specific path
     *
     * @param name RLock name
     * @param path rate limit key
     */
    public void execute(String name, String path) {
        RateLimitRepository repository = (RateLimitRepository) BeanManager.getBean(RateLimitRepository.class);
        RedissonClient redisson = (RedissonClient) BeanManager.getBean(RedissonClient.class);
        RequestContext ctx = RequestContext.getCurrentContext();

        RLock lock = redisson.getLock(name);
        lock.lock();

        RateLimit rate = repository.find(path);
        if (rate.getLastRequest() == null) {
            rate.setLastRequest(LocalDateTime.now());
        }

        if (hasIntervalEnded(rate)) {
            rate.reset();
            rate.decreaseRemaining();
            repository.save(rate);
        } else {
            if (rate.hasRemaining()) {
                rate.decreaseRemaining();
                repository.save(rate);
            } else {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                ctx.setResponseBody(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            }

        }
        lock.unlock();
    }

    /*
     * Checks if the limiting time has ended
     */
    public boolean hasIntervalEnded(RateLimit rate) {

        switch (rate.getInterval()) {
            case SECONDS:
                return Duration.between(LocalDateTime.now(), rate.getLastRequest()).getSeconds() > 0;

            case MINUTES:
                return Duration.between(LocalDateTime.now(), rate.getLastRequest()).toMinutes() > 0;

            case HOURS:
                return Duration.between(LocalDateTime.now(), rate.getLastRequest()).toHours() > 0;

            default:
                return false;
        }
    }

}
