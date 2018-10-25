package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.util.ConstantsInterceptors;
import br.com.conductor.heimdall.gateway.filter.helper.HelperImpl;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.middleware.spec.Helper;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Service;

@Service
public class MockInterceptorService {

    public void execute(Integer status, String body) {

        Helper helper = new HelperImpl();
        RequestContext ctx = RequestContext.getCurrentContext();

        String response;
        if (helper.json().isJson(body)) {
            response = helper.json().parse(body);

            helper.call().response().header().add("Content-Type", "application/json");
        } else {

            response = body;

            helper.call().response().header().add("Content-Type", "text/plain");
        }

        helper.call().response().setBody(response);

        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(status);

        TraceContextHolder.getInstance().getActualTrace().trace(ConstantsInterceptors.GLOBAL_MOCK_INTERCEPTOR_LOCALIZED, response);
    }
}
