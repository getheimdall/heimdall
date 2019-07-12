/*-
 * =========================LICENSE_START==================================
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
package br.com.conductor.heimdall.core.trace;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import net.logstash.logback.marker.LogstashMarker;

public class MessageDelegate {
	
	public static void send(Logger logger, int resultStatus, String message) {
		if (isInfo(resultStatus)) {

			logger.info(message);
        } else if (isWarn(resultStatus)) {

        	logger.warn(message);
        } else {

        	logger.error(message);
        }
	}

	public static void send(Logger logger, int resultStatus, LogstashMarker append, String message) {
		if (isInfo(resultStatus)) {

			logger.info(append, message);
        } else if (isWarn(resultStatus)) {

        	logger.warn(append, message);
        } else {

        	logger.error(append, message);
        }
	}
	
	/*
     * Checks if the status code is in range 1xx to 2xx
     */
    private static boolean isInfo(Integer statusCode) {
        return HttpStatus.valueOf(statusCode).is1xxInformational() ||
                HttpStatus.valueOf(statusCode).is2xxSuccessful();
    }

    /*
     * Checks if the status code is in range 3xx to 4xx
     */
    private static boolean isWarn(Integer statusCode) {
        return HttpStatus.valueOf(statusCode).is3xxRedirection() ||
                HttpStatus.valueOf(statusCode).is4xxClientError();
    }

}
