
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import org.springframework.stereotype.Component;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 
 * @author marcos.filho
 *
 */

@Component
public class CorsFilter extends ZuulFilter {

	private Map<String, String> cors;

	public CorsFilter() {
		this.cors = new HashMap<>();
		this.cors.put("Access-Control-Allow-Origin", "*");
		this.cors.put("Access-Control-Allow-Credentials", "true");
		this.cors.put("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, DELETE, OPTIONS");
		this.cors.put("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential");
		this.cors.put("Access-Control-Max-Age", "3600");
	}
	
	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();

		HttpServletResponse response = ctx.getResponse();
		List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
		
		List<String> headersFromResponse = zuulResponseHeaders.stream().map(Pair::first).collect(Collectors.toList());
		
		cors.entrySet()
			.stream()
			.filter(entry -> !headersFromResponse.contains(entry.getKey()))
			.forEach(entry -> response.setHeader(entry.getKey(), entry.getValue()));
		
		return null;
	}

	@Override
	public int filterOrder() {
		return 101;
	}

	@Override
	public String filterType() {
		return POST_TYPE;
	}
	
}
