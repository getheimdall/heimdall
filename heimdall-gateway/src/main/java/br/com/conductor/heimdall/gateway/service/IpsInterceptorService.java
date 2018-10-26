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
package br.com.conductor.heimdall.gateway.service;

import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Provides methods to validate request with blacklist and whitelist interceptors.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class IpsInterceptorService {

    /**
     * Checks if the request ip is in the whitelist
     *
     * @param whitelist Set of allowed ids
     * @throws Throwable
     */
    public void executeWhiteList(Set<String> whitelist) throws Throwable {

        RequestContext ctx = RequestContext.getCurrentContext();
        Set<String> requestIps = getIpFromRequest(ctx.getRequest());
        Set<String> common = new HashSet<>();

        requestIps.forEach(s -> {
            if (!whitelist.contains(s))
                common.add(s);
        });

        if (!common.isEmpty()) {
            ctx.getResponse().sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized ip list: " + common.toString());
        }
    }

    /**
     * Checks if the request ip is in the blacklist
     *
     * @param blacklist Set of blocked ids
     * @throws Throwable
     */
    public void executeBlackList(Set<String> blacklist) throws Throwable {

        RequestContext ctx = RequestContext.getCurrentContext();
        Set<String> requestIps = getIpFromRequest(ctx.getRequest());
        Set<String> common = new HashSet<>();

        requestIps.forEach(s -> {
            if (blacklist.contains(s))
                common.add(s);
        });

        if (!common.isEmpty()) {
            ctx.getResponse().sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized ip list: " + common.toString());
        }
    }

    /**
     * Method that recovers Ips from {@link HttpServletRequest}
     *
     * @param req The {@link HttpServletRequest}
     * @return {@link Set}<{@link String}> of request Ips
     */
    private Set<String> getIpFromRequest(HttpServletRequest req) {

        Set<String> ipsFromRequest = new HashSet<>();

        if (Objects.nonNull(req)) {

            final String x_FORWARDED_FOR = "X-FORWARDED-FOR";

            if (Objects.nonNull(req.getHeader(x_FORWARDED_FOR))){
                Arrays.stream(req.getHeader(x_FORWARDED_FOR).split(","))
                        .forEach(ip -> ipsFromRequest.add(ip.trim()));
            }

            ipsFromRequest.add(req.getRemoteAddr());
        }

        return ipsFromRequest;
    }
}
