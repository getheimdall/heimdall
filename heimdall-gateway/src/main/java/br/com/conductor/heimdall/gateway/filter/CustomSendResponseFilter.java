
package br.com.conductor.heimdall.gateway.filter;

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

import org.springframework.cloud.netflix.zuul.filters.post.SendResponseFilter;

import com.netflix.zuul.context.RequestContext;

import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.trace.FilterDetail;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * Extends the {@link SendResponseFilter} to add a timelimit to the response
 * filter.
 * 
 * @author Marcos Filho
 *
 */
@Slf4j
public class CustomSendResponseFilter extends SendResponseFilter {

	private FilterDetail detail = new FilterDetail();

	@Override
	public boolean shouldFilter() {

		long startTime = System.currentTimeMillis();

		RequestContext context = RequestContext.getCurrentContext();
		boolean should = (!context.getZuulResponseHeaders().isEmpty() || context.getResponseDataStream() != null || context.getResponseBody() != null);
		// boolean should = super.shouldFilter();

		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);

		detail.setTimeInMillisShould(duration);
		return should;
	}

	@Override
	public Object run() {
		long startTime = System.currentTimeMillis();
		try {
			Object obj = super.run();
			detail.setStatus(Constants.SUCCESS);
			return obj;
		} catch (Exception e) {
			detail.setStatus(Constants.FAILED);
			log.error("Error during send response", e);
			throw e;
		} finally {
			long endTime = System.currentTimeMillis();

			long duration = (endTime - startTime);

			detail.setName(this.getClass().getSimpleName());
			detail.setTimeInMillisRun(duration);
			TraceContextHolder.getInstance().getActualTrace().addFilter(detail);
		}
	}
}
