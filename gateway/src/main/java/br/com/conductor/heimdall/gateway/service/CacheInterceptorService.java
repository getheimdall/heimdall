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
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.entity.ApiResponse;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import com.netflix.zuul.context.RequestContext;
import org.assertj.core.util.Lists;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.conductor.heimdall.core.util.ConstantsCache.CACHE_BUCKET;
import static br.com.conductor.heimdall.core.util.ConstantsCache.CACHE_TIME_TO_LIVE;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.API_ID;
import static br.com.conductor.heimdall.gateway.util.ConstantsContext.API_NAME;

/**
 * Cache service provides methods to create and delete a response cache from a request.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class CacheInterceptorService {

	@Autowired
	private RedissonClient redissonClientCacheInterceptor;

    /**
     * Checks if the request is in cache. If true then returns the cached response, otherwise
     * continues the request normally and signals to create the cache for this request.
     *
     * @param cacheName   Cache name provided
     * @param timeToLive  How much time the cache will live (0 or less to live forever)
     * @param headers     List of headers that when present signal that the request should be cached
     * @param queryParams List of queryParams that when present signal that the request should be cached
     */
    public void cacheInterceptor(String cacheName, Long timeToLive, List<String> headers, List<String> queryParams) {

        RequestContext context = RequestContext.getCurrentContext();

        boolean responseFromCache = false;

        if (shouldCache(context, headers, queryParams)) {
            RBucket<ApiResponse> rBucket = redissonClientCacheInterceptor.getBucket(createCacheKey(context, cacheName, headers, queryParams));

            if (rBucket.get() == null) {
                context.put(CACHE_BUCKET, rBucket);
                context.put(CACHE_TIME_TO_LIVE, timeToLive);
            } else {
                ApiResponse response = rBucket.get();

                HttpServletResponse r = context.getResponse();

                response.getHeaders().forEach(r::addHeader);
                context.setResponseBody(response.getBody());
                context.getResponse().setStatus(response.getStatus());


                context.setSendZuulResponse(false);
                responseFromCache = true;
            }
        }

        TraceContextHolder.getInstance().getActualTrace().setCache(responseFromCache);
    }

    /**
     * Clears a cache if it exists
     *
     * @param cacheName Cache name provided
     */
    public void cacheClearInterceptor(String cacheName) {
        RequestContext context = RequestContext.getCurrentContext();

        redissonClientCacheInterceptor.getKeys().deleteByPattern(createDeleteCacheKey(context, cacheName));
    }

    /*
     * Creates the cache key
     */
    private String createCacheKey(RequestContext context, String cacheName, List<String> headers, List<String> queryParams) {

        StringBuilder cacheKey = new StringBuilder();
        cacheKey.append(context.get(API_ID));
        cacheKey.append("-");
        cacheKey.append(context.get(API_NAME));
        cacheKey.append(":");
        cacheKey.append(cacheName);
        cacheKey.append(":");
        cacheKey.append(context.getRequest().getRequestURL().toString());

        if (headers != null && !headers.isEmpty()) {
            cacheKey.append(":headers=");
            StringBuilder headersBuilder = new StringBuilder();
            headers.forEach(s -> headersBuilder
                    .append(s)
                    .append("=")
                    .append(context.getRequest().getHeader(s))
                    .append(":")
            );

            cacheKey.append(headersBuilder.toString());
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            cacheKey.append(":queryParams=");
            StringBuilder queryParamsBuilder = new StringBuilder();
            queryParams.forEach(s -> queryParamsBuilder
                    .append(s)
                    .append("=")
                    .append(context.getRequestQueryParams().get(s))
                    .append(":")
            );

            cacheKey.append(queryParamsBuilder.toString());
        }

        return cacheKey.toString();
    }

    /*
     * Creates a cache key to delete the cache
     */
    private String createDeleteCacheKey(RequestContext context, String cacheName) {

        return context.get(API_ID) + "-" +
                context.get(API_NAME) + ":" +
                cacheName + ":*";
    }

    /*
     * Defines if the cache should be written
     */
    private boolean shouldCache(RequestContext context, List<String> headers, List<String> queryParams) {

        Map<String, List<String>> requestQueryParams = (context.getRequestQueryParams() != null) ? context.getRequestQueryParams() : new HashMap<>();

        boolean queries = Lists.newArrayList(requestQueryParams.keySet()).containsAll(queryParams);

        boolean header = (context.getRequest().getHeaderNames() != null) && Collections.list(context.getRequest().getHeaderNames()).containsAll(headers);

        return header && queries;
    }

}
