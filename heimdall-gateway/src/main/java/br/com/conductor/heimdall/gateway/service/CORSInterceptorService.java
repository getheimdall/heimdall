package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.middleware.enums.HttpStatus;
import com.netflix.util.Pair;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CORSInterceptorService {

    public void executeCorsPreFilter(RequestContext ctx, Map<String, String> cors) {
        if (ctx.getRequest().getMethod().equals(HttpMethod.OPTIONS.name())) {
            addHeadersToResponse(ctx, cors);
            ctx.getResponse().setStatus(HttpStatus.OK.value());
            ctx.setSendZuulResponse(false);
        } else {
            ctx.set("CORSApply", cors);
        }
    }

    public void executeCorsPostFilter(RequestContext ctx, Map<String, String> cors) {
        addHeadersToResponse(ctx, cors);
    }

    private void addHeadersToResponse(RequestContext ctx, Map<String, String> cors) {
        HttpServletResponse response = ctx.getResponse();
        List<Pair<String, String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
        List<String> headersFromResponse = zuulResponseHeaders.stream().map(Pair::first).collect(Collectors.toList());

        cors.entrySet()
                .stream()
                .filter(entry -> !headersFromResponse.contains(entry.getKey()))
                .forEach(entry -> response.setHeader(entry.getKey(), entry.getValue()));
    }
}
