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
package br.com.conductor.heimdall.core.repository.impl;

import br.com.conductor.heimdall.core.entity.RateLimit;
import br.com.conductor.heimdall.core.enums.Interval;
import br.com.conductor.heimdall.core.repository.RateLimitRepository;
import br.com.conductor.heimdall.core.util.ConstantsCache;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * This class provides methods to save, find and delete a {@link RateLimit}.
 *
 * @author Marcos Filho
 */
@Repository
public class RateLimitRepositoryImpl implements RateLimitRepository {

    private RMap<String, RateLimit> map;

    private final RedissonClient redissonClientRateLimitInterceptor;

    public RateLimitRepositoryImpl(RedissonClient redissonClientRateLimitInterceptor) {
        this.redissonClientRateLimitInterceptor = redissonClientRateLimitInterceptor;
    }

    @PostConstruct
    private void init() {

        if (redissonClientRateLimitInterceptor != null) {
            map = redissonClientRateLimitInterceptor.getMap(RateLimit.KEY);
        }

    }

    @Override
    public RateLimit save(RateLimit rate) {

        if (map != null) {
            map.put(rate.getPath(), rate);
        }
        return find(rate.getPath());
    }

    @Override
    public RateLimit find(String path) {

        RateLimit rateLimit = null;
        if (map != null) {
            rateLimit = map.get(path);
        }
        return rateLimit;
    }

    @Override
    public void delete(String path) {

        if (map != null) {
            map.remove(path);
        }
    }

    @Override
    public RLock getLock(String key) {
        return redissonClientRateLimitInterceptor.getLock(key);
    }

    @Override
    public RateLimit mountRatelimit(String interceptorId, Long calls, Interval interval) {

        String path = ConstantsCache.RATE_LIMIT_KEY_PREFIX + interceptorId;

        RateLimit rate = new RateLimit(path, calls, interval);
        return this.save(rate);
    }
}
