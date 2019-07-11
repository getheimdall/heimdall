package br.com.conductor.heimdall.gateway.listener;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.service.InterceptorService;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AddInterceptorsListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(AddInterceptorsListener.class);

    @Autowired
    private InterceptorService interceptorService;

    @Autowired
    private InterceptorFileService interceptorFileService;

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
