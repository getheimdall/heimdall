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
package br.com.conductor.heimdall.core.publisher;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.util.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Sends messages to create, remove and refresh the interceptors
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
@Slf4j
public class RedisInterceptorPublisher {

    private final RedisMessagePublisher redisMessagePublisher;

    public RedisInterceptorPublisher(RedisMessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    /**
     * Dispatch a message to create a {@link Interceptor}
     *
     * @param id {@link Interceptor} id
     */
    public void dispatchInterceptor(String id) {

        redisMessagePublisher.publish(RedisConstants.INTERCEPTORS_ADD, id);
        log.debug("Add/Update interceptor");
    }

    /**
     * Dispatch a message to refresh all interceptors
     */
    public void dispatchRefreshAllInterceptors() {

        redisMessagePublisher.publish(RedisConstants.INTERCEPTORS_REFRESH, "");
        log.debug("Refresh all interceptors");

    }

    /**
     * Dispatch a message to remove a {@link Interceptor}
     *
     * @param id The {@link Interceptor} id
     */
    public void dispatchRemoveInterceptors(String id) {

        redisMessagePublisher.publish(RedisConstants.INTERCEPTORS_REMOVE, id);
        log.debug("Remove interceptor");
    }

}
