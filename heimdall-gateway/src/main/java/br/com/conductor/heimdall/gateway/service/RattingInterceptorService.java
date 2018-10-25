package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.entity.RateLimit;
import br.com.conductor.heimdall.core.enums.Interval;
import br.com.conductor.heimdall.core.repository.RateLimitRepository;
import br.com.conductor.heimdall.core.util.BeanManager;
import com.netflix.zuul.context.RequestContext;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class RattingInterceptorService {

    public void execute(String name, String path) {
        RateLimitRepository repository = (RateLimitRepository) BeanManager.getBean(RateLimitRepository.class);
        RedissonClient redisson = (RedissonClient) BeanManager.getBean(RedissonClient.class);
        RequestContext ctx = RequestContext.getCurrentContext();

        RLock lock = redisson.getLock(name);
        lock.lock();

        RateLimit rate = repository.find(path);
        if (rate.getLastRequest() == null) {
            rate.setLastRequest(LocalDateTime.now());
        }


        if (isIntervalEnded(rate)) {
            rate.reset();
            rate.decreaseRemaining();
            repository.save(rate);
        } else {
            if (rate.hasRemaining()) {
                rate.decreaseRemaining();
                repository.save(rate);
            } else {
                ctx.setSendZuulResponse(false);
                ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                ctx.setResponseBody(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            }

        }
        lock.unlock();
    }

    private boolean isIntervalEnded(RateLimit rate) {

        if (rate.getInterval() == Interval.SECONDS) {
            return Duration.between(LocalDateTime.now(), rate.getLastRequest()).getSeconds() != 0;
        } else if (rate.getInterval() == Interval.MINUTES) {
            return Duration.between(LocalDateTime.now(), rate.getLastRequest()).toMinutes() != 0;
        } else {
            return Duration.between(LocalDateTime.now(), rate.getLastRequest()).toHours() != 0;
        }
    }

}
