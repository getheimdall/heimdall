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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Listener that controls the {@link Interceptor} repository.
 *
 * @author Filipe Germano
 */
@Slf4j
@Component
public class RefreshInterceptorsListener implements MessageListener {

    private final StartServer startServer;

    public RefreshInterceptorsListener(StartServer startServer) {
        this.startServer = startServer;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Refresh all Interceptors");

        try {

            startServer.initApplication();
        } catch (Exception e) {

            log.error(e.getMessage(), e);
        }
    }
}
