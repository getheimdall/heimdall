
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

import br.com.conductor.heimdall.gateway.filter.helper.ApiResponseImpl;
import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import com.google.common.collect.Maps;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.redisson.api.RMap;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

import static br.com.conductor.heimdall.gateway.util.Constants.REDIS_MAP_CACHE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * Filter to create a cache. When this filter is enabled it will create a cache from the response in Redis.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Component
public class CacheWriterFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 105;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        Boolean should = false;

        if (context.get(REDIS_MAP_CACHE) != null)
            should = (Boolean) context.get(REDIS_MAP_CACHE);

        return should;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();

        RMap<String, ApiResponse> map = (RMap<String, ApiResponse>) context.get("cache-map");
        String key = (String) context.get("cache-key");

        HttpServletResponse response = context.getResponse();

        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(getResponseHeaders(response));
        apiResponse.setBody(context.getResponseBody());
        apiResponse.setStatus(response.getStatus());

        map.put(key, apiResponse);
        return null;
    }

    /*
     * Copies the response headers from the HttpServletResponse to a Map
     */
    private Map<String, String> getResponseHeaders(HttpServletResponse response) {

        Map<String, String> map = Maps.newHashMap();
        Collection<String> headerNames = response.getHeaderNames();

        headerNames.forEach(s -> map.put(s, response.getHeader(s)));

        return map;
    }
}
