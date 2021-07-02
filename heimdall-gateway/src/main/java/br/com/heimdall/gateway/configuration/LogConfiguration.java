/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 *
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
package br.com.heimdall.gateway.configuration;

import java.time.ZoneId;

import javax.annotation.PostConstruct;

import br.com.heimdall.gateway.appender.MongoDBAppender;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import br.com.heimdall.core.environment.Property;
import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;

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

	private static final String DEFAULT_ZONE_ID = ZoneId.systemDefault().getId();

	@Autowired
	private Property property;

	@PostConstruct
	public void onStartUp() {

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		if (property.getMongo().getEnabled()) {

			Logger logger = (Logger) LoggerFactory.getLogger("mongo");
			logger.setAdditive(false);

			String zoneId = property.getMongo().getZoneId() != null ? property.getMongo().getZoneId() : DEFAULT_ZONE_ID;

			// Creating custom MongoDBAppender
			Appender<ILoggingEvent> appender;
			if (property.getMongo().getUrl() != null) {
				appender = new MongoDBAppender(property.getMongo().getUrl(), property.getMongo().getDataBase(), property.getMongo().getCollection(), zoneId);
			} else {
				appender = new MongoDBAppender(property.getMongo().getServerName(), property.getMongo().getPort(), property.getMongo().getDataBase(), property.getMongo().getCollection(), zoneId);
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

		if (property.getLogstash().getEnabled()) {

			Logger logger = (Logger) LoggerFactory.getLogger("logstash");
			logger.setAdditive(false);

			LogstashTcpSocketAppender appender = new LogstashTcpSocketAppender();
			appender.addDestination(property.getLogstash().getDestination());

			LogstashEncoder encoder = new LogstashEncoder();
			appender.setEncoder(encoder);
			appender.setContext(lc);
			appender.start();

			logger.addAppender(appender);

		}
	}

}
