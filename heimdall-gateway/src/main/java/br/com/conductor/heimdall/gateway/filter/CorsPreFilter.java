package br.com.conductor.heimdall.gateway.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import br.com.conductor.heimdall.gateway.service.CORSInterceptorService;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Api Interceptor without cors.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Component
public class CorsPreFilter extends HeimdallFilter {

    @Autowired
    private CORSInterceptorService corsInterceptorService;

    private Map<String, String> cors;

    public CorsPreFilter() {
        this.cors = new HashMap<>();
        this.cors.put("Access-Control-Allow-Origin", "*");
        this.cors.put("Access-Control-Allow-Credentials", "true");
        this.cors.put("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, DELETE, OPTIONS");
        this.cors.put("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential");
        this.cors.put("Access-Control-Max-Age", "3600");
    }


    @Override
    public boolean should() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return (boolean) ctx.get(ConstantsContext.CORS_FILTER_DEFAULT);
    }

    @Override
    public void execute() throws Throwable {
        corsInterceptorService.executeCorsPreFilter(cors);
    }

    @Override
    public String getName() {
        return "CorsPreFilterDefault";
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 20;
    }
}
