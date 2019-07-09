package br.com.conductor.heimdall.gateway.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class represents the counter for the overall time limit in middleware requests.
 *
 * @author cassio.espindola
 */
@Configuration
public class TimeoutCounter {

     @Getter
     @Setter
     @Value("${zuul.host.socket-timeout-millis}")
     private long counter;

     public void decrementCounter(long timeToDecrement) {

          this.setCounter(getCounter() - timeToDecrement);
     }
}
