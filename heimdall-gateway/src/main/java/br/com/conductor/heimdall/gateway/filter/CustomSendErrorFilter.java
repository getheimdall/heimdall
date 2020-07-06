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
package br.com.conductor.heimdall.gateway.filter;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomSendErrorFilter extends SendErrorFilter {

	private static final String MESSAGE = "message";
	private static final String ERROR = "error";

	@Override
	public Object run() {
		try {
			RequestContext ctx = RequestContext.getCurrentContext();
			ZuulException exception = getZuulException(ctx.getThrowable());

			HttpServletRequest request = ctx.getRequest();

			if (exception.getCause() != null) {
				log.error("Error catched: {}", exception.getCause().getMessage());
			} else {
				log.error("Error during filtering: {}", exception.getMessage());
			}

			String message = null;
			if (exception.getCause() != null && StringUtils.hasText(exception.getCause().getMessage())) {
				message = exception.getCause().getMessage();
			}

			Map<String, Object> errorAttributes = new LinkedHashMap<String, Object>();
			errorAttributes.put("timestamp", LocalDateTime.now());
			final int status = exception.nStatusCode;
			errorAttributes.put("status", status);

			try {
				errorAttributes.put(ERROR, HttpStatus.valueOf(status).getReasonPhrase());
			} catch (Exception ex) {
				errorAttributes.put(ERROR, "Http Status " + status);
			}

			HeimdallException exceptionHeimdall = new HeimdallException(ExceptionMessage.GLOBAL_ERROR_ZUUL);

			errorAttributes.put("exception", exceptionHeimdall.getClass().getSimpleName());
			errorAttributes.put(MESSAGE, exception.getMessage());

			if ((!StringUtils.isEmpty(message) || errorAttributes.get(MESSAGE) == null)) {
				errorAttributes.put(MESSAGE, StringUtils.isEmpty(message) ? "No message available" : message);
			}

			String path = (String) request.getAttribute("javax.servlet.error.request_uri");
			if (path != null) {
				errorAttributes.put("path", path);
			}

			ctx.getResponse().setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			ctx.setResponseBody(new JSONObject(errorAttributes).toString());
			ctx.setResponseStatusCode(status);

		} catch (Exception ex) {
			ReflectionUtils.rethrowRuntimeException(ex);
		}
		return null;
	}

	public ZuulException getZuulException(Throwable throwable) {

		if (throwable.getCause() instanceof ZuulRuntimeException) {
			// this was a failure initiated by one of the local filters
			return (ZuulException) throwable.getCause().getCause();
		}

		if (throwable.getCause() instanceof ZuulException) {
			// wrapped zuul exception
			return (ZuulException) throwable.getCause();
		}

		if (throwable instanceof ZuulException) {
			// exception thrown by zuul lifecycle
			return (ZuulException) throwable;
		}

		// fallback, should never get here
		return new ZuulException(throwable, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
	}
}
