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
package br.com.conductor.heimdall.gateway.configuration;

import br.com.conductor.heimdall.core.environment.Property;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private Property property;

	@PostConstruct
	public void onStartUp() {

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();


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
