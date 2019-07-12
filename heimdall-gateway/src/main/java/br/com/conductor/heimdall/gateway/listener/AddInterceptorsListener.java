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
package br.com.conductor.heimdall.gateway.listener;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.service.InterceptorService;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddInterceptorsListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(AddInterceptorsListener.class);

    private final InterceptorService interceptorService;

    private final InterceptorFileService interceptorFileService;

    public AddInterceptorsListener(InterceptorService interceptorService, InterceptorFileService interceptorFileService) {
        this.interceptorService = interceptorService;
        this.interceptorFileService = interceptorFileService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer redisSerializer = new JdkSerializationRedisSerializer();
        final String interceptorId = (String) redisSerializer.deserialize(message.getBody());
        Interceptor interceptor = interceptorService.find(interceptorId);
        if (Objects.nonNull(interceptor)) {

            log.info("Updating/Creating Interceptor id: " + interceptorId);
            interceptorFileService.createFileInterceptor(interceptor);
        } else {

            log.info("It was not possible Updating/Creating Interceptor id: " + interceptorId);
        }
    }
}
