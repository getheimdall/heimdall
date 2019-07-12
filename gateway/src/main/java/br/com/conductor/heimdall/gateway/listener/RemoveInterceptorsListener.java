package br.com.conductor.heimdall.gateway.listener;

import br.com.conductor.heimdall.core.dto.InterceptorFileDTO;
import br.com.conductor.heimdall.gateway.service.InterceptorFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RemoveInterceptorsListener implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RemoveInterceptorsListener.class);

    @Autowired
    private InterceptorFileService interceptorFileService;

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
