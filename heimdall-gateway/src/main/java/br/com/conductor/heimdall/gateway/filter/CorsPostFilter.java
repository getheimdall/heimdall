
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

import br.com.conductor.heimdall.gateway.service.CORSInterceptorService;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * Api Interceptor without cors.
 *
 * @author marcos.filho
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */

@Component
public class CorsPostFilter extends ZuulFilter {

    @Autowired
    private CORSInterceptorService corsInterceptorService;

	private Map<String, String> cors;

	public CorsPostFilter() {
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

        Map<String, String> corsByInterceptorMustache = (Map<String, String>) ctx.get(ConstantsContext.CORS_FILTER);

        if (Objects.nonNull(corsByInterceptorMustache)) {
            corsInterceptorService.executeCorsPostFilter(corsByInterceptorMustache);
        } else {
            corsInterceptorService.executeCorsPostFilter(cors);
        }

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