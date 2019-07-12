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

import br.com.conductor.heimdall.gateway.configuration.HeimdallHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RefreshRoutesListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RefreshRoutesListener.class);

    private final HeimdallHandlerMapping heimdallHandlerMapping;

    private final StartServer startServer;

    public RefreshRoutesListener(HeimdallHandlerMapping heimdallHandlerMapping, StartServer startServer) {
        this.heimdallHandlerMapping = heimdallHandlerMapping;
        this.startServer = startServer;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("Updating Zuul Routes");
            heimdallHandlerMapping.setDirty(false);
            startServer.initApplication();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
