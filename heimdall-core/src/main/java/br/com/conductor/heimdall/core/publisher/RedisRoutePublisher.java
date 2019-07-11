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

import br.com.conductor.heimdall.core.util.RedisConstants;
import org.springframework.stereotype.Service;

/**
 * Publisher to refresh the routes.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class RedisRoutePublisher {

    private final RedisMessagePublisher redisMessagePublisher;

    public RedisRoutePublisher(RedisMessagePublisher redisMessagePublisher) {
        this.redisMessagePublisher = redisMessagePublisher;
    }

    /**
     * Dispatch a message to refresh zuul routes
     */
    public void dispatchRoutes() {

        redisMessagePublisher.publish(RedisConstants.ROUTES_REFRESH, "");
    }

}
