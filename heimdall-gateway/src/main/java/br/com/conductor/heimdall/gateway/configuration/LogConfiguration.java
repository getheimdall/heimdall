
package br.com.conductor.heimdall.gateway.configuration;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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

import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.gateway.appender.MongoDBAppender;
import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Class responsible to configure the logging.
 *
 * @author Thiago Sampaio
 * @author Marcos Filho
 * @author Marcelo Aguiar Rodrigues
 *
 */
@Configuration
public class LogConfiguration {

     private static final int DEFAULT_QUEUE_SIZE = 500;

     @Autowired
     private Property property;
     
     @PostConstruct
     public void onStartUp() {
    	 if (property.getMongo().getEnabled()) {
    		 
             LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
             Logger logger = (Logger) LoggerFactory.getLogger("mongo");
             logger.setAdditive(false);

             // Creating custom MongoDBAppender
             Appender<ILoggingEvent> appender;
             if (property.getMongo().getUrl() != null) {
          	   appender = new MongoDBAppender(property.getMongo().getUrl(), property.getMongo().getDataBase(), property.getMongo().getCollection(), property.getMongo().getZoneId());
             } else {
          	   appender = new MongoDBAppender(property.getMongo().getServerName(), property.getMongo().getPort(), property.getMongo().getDataBase(), property.getMongo().getCollection(), property.getMongo().getZoneId());
             }
             appender.setContext(lc);
             appender.start();

             // Creating AsyncAppender
             int queueSize = (property.getMongo().getQueueSize() != null) ? property.getMongo().getQueueSize().intValue() : DEFAULT_QUEUE_SIZE;

             AsyncAppender asyncAppender = new AsyncAppender();
             asyncAppender.setQueueSize(queueSize);
             if (property.getMongo().getDiscardingThreshold() != null) {            	 
            	 asyncAppender.setDiscardingThreshold(property.getMongo().getDiscardingThreshold().intValue());
             }
             asyncAppender.addAppender(appender);
             asyncAppender.start();

             logger.addAppender(asyncAppender);
        }
     }

     /**
      * Returns the proper logging strategy.
      * @return		
      */
     @Bean
     public Logger loggerTrace() {

          if (property.getSplunk().getEnabled()) {

               LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
               Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

               LogstashTcpSocketAppender appender = new net.logstash.logback.appender.LogstashTcpSocketAppender();
               appender.addDestination(property.getSplunk().getDestination());

               LogstashEncoder encoder = new net.logstash.logback.encoder.LogstashEncoder();

               appender.setEncoder(encoder);
               appender.setContext(lc);
               appender.start();

               logger.addAppender(appender);

          }

          return null;

     }

}
