package br.com.conductor.heimdall.gateway.listener;

import br.com.conductor.heimdall.gateway.configuration.HeimdallHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RefreshRoutesListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RefreshRoutesListener.class);

    @Autowired
    private HeimdallHandlerMapping heimdallHandlerMapping;

    @Autowired
    private StartServer startServer;

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
