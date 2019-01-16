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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.netflix.zuul.context.RequestContext;

/**
 * Provides methods to validate request with blacklist and whitelist interceptors.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class IpsInterceptorService {

	private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
	private boolean isAuthorized;

    /**
     * Checks if the request ip is in the whitelist
     *
     * @param whitelist Set of allowed ids
     * @throws Throwable
     */
    public void executeWhiteList(Set<String> whitelist) throws Throwable {
    	isAuthorized = false;

        RequestContext ctx = RequestContext.getCurrentContext();
        Set<String> requestIps = getIpFromRequest(ctx.getRequest());
        
        Set<String> ipsWithoutMask = new HashSet<>();
        Set<String> ipsWithMask = new HashSet<>();
        
        classifyIps(whitelist, ipsWithMask, ipsWithoutMask);
        
        ipsWithMask.forEach(ipWithMask -> {
        	requestIps.forEach(ip -> {
        		if (isInterDomainRouting(ipWithMask, ip)) {
        			isAuthorized = true;
        		}
        	});
        });

        if (ipsWithoutMask.stream().anyMatch(requestIps::contains)) {
        	isAuthorized = true;
        }

        if (!isAuthorized) {
            ctx.getResponse().sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized ip");
        }
    }
    
    /**
     * Removing port from ip's used during request
     * @param requestIps
     * @return
     */
	private Set<String> sanitizeIps(Set<String> requestIps) {
		return requestIps.stream().map(ip -> {
			if (ip.contains(":")) {
				return StringUtils.substringBefore(ip, ":");
			}
			return ip;
		}).collect(Collectors.toSet());
	}

	/**
	 * Check if ip is present inside CIDR Range, network and broadcast addresses are to be included.
	 * @param cidr
	 * @param ip
	 * @return
	 */
	private boolean isInterDomainRouting(String cidr, String ip) {
    	SubnetUtils subnet = new SubnetUtils(cidr);
		subnet.setInclusiveHostCount(true);
		
		return subnet.getInfo().isInRange(ip);
    }
	

	/**
	 * Separate ip's with '/' (CIDR) to another Set list, providing better flexibility to identify the ip's match.
	 * @param iplist
	 * @param ipsWithMask
	 * @param ipsWithoutMask
	 */
    private void classifyIps(Set<String> iplist, Set<String> ipsWithMask, Set<String> ipsWithoutMask) {
    	for (String ip : iplist) {
			if (ip.contains("/")) {
				ipsWithMask.add(ip);
			} else {				
				ipsWithoutMask.add(ip);
			}
		}
		
	}

	/**
     * Checks if the request ip is in the blacklist
     *
     * @param blacklist Set of blocked ids
     * @throws Throwable
     */
	public void executeBlackList(Set<String> blacklist) throws Throwable {
		isAuthorized = true;

		RequestContext ctx = RequestContext.getCurrentContext();
		Set<String> requestIps = getIpFromRequest(ctx.getRequest());

		Set<String> ipsWithoutMask = new HashSet<>();
		Set<String> ipsWithMask = new HashSet<>();

		classifyIps(blacklist, ipsWithMask, ipsWithoutMask);

		ipsWithMask.forEach(ipWithMask -> {
			requestIps.forEach(ip -> {
				if (isInterDomainRouting(ipWithMask, ip)) {
					isAuthorized = false;
				}
			});
		});

		if (ipsWithoutMask.stream().anyMatch(requestIps::contains)) {
			isAuthorized = false;
		}

		if (!isAuthorized) {
			ctx.getResponse().sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized ip");
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

			if (Objects.nonNull(req.getHeader(X_FORWARDED_FOR))) {
				Arrays.stream(req.getHeader(X_FORWARDED_FOR).split(",")).forEach(ip -> ipsFromRequest.add(ip.trim()));
			}

			ipsFromRequest.add(req.getRemoteAddr());
		}

		return sanitizeIps(ipsFromRequest);
	}
}
