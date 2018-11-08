
package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.util.BeanManager;
import br.com.conductor.heimdall.gateway.service.LifeCycleService;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class CorsFilterPre extends ZuulFilter {

    private static Set<String> pathsAllowed;
    private static Set<String> pathsNotAllowed;
    private static String inboundURL = "";
    private static Long referenceId = 1L;

    private static Map<String, String> cors;

    public CorsFilterPre() {
        pathsAllowed = new HashSet<>();
        pathsNotAllowed = new HashSet<>();
        cors = new HashMap<>();
        cors.put("Access-Control-Allow-Origin", "*");
        cors.put("Access-Control-Allow-Credentials", "true");
        cors.put("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, DELETE, OPTIONS");
        cors.put("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential");
        cors.put("Access-Control-Max-Age", "3600");
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        LifeCycleService lifeCycleService = (LifeCycleService) BeanManager.getBean(LifeCycleService.class);
        return lifeCycleService.should(InterceptorLifeCycle.API, pathsAllowed, pathsNotAllowed, inboundURL, request.getMethod(), request, referenceId);
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        if (ctx.getRequest().getMethod().equals(HttpMethod.OPTIONS.name())) {
            HttpServletResponse response = ctx.getResponse();
            List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();

            List<String> headersFromResponse = zuulResponseHeaders.stream().map(Pair::first).collect(Collectors.toList());

            cors.entrySet()
                    .stream()
                    .filter(entry -> !headersFromResponse.contains(entry.getKey()))
                    .forEach(entry -> response.setHeader(entry.getKey(), entry.getValue()));
        }


        return null;
    }

    @Override
    public int filterOrder() {
        return 101;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

}
