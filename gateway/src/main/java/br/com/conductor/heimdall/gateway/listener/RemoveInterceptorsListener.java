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

import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RemoveInterceptorsListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RemoveInterceptorsListener.class);

    private final InterceptorFileService interceptorFileService;

    public RemoveInterceptorsListener(InterceptorFileService interceptorFileService) {
        this.interceptorFileService = interceptorFileService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        RedisSerializer redisSerializer = new JdkSerializationRedisSerializer();
        final String deserialize = (String) redisSerializer.deserialize(message.getBody());
        InterceptorFileDTO interceptorFileDTO = new InterceptorFileDTO();

        final String[] split = deserialize.split("\\|");

        interceptorFileDTO.setId(split[0]);
        interceptorFileDTO.setPath(split[1]);
        log.info("Removing Interceptor id: " + interceptorFileDTO.getId());

        interceptorFileService.removeFileInterceptor(interceptorFileDTO);

    }
}
