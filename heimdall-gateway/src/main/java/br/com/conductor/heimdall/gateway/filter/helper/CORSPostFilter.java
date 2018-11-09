package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.service.CORSInterceptorService;
import br.com.conductor.heimdall.gateway.trace.FilterDetail;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

@Component
@Slf4j
public class CORSPostFilter extends ZuulFilter {

    @Autowired
    private CORSInterceptorService corsInterceptorService;

    private FilterDetail detail = new FilterDetail();

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        return Objects.nonNull(ctx.get("CORSApply"));
    }

    @Override
    public Object run() {
        long startTime = System.currentTimeMillis();

        try {
            process();
            detail.setStatus(Constants.SUCCESS);
        } catch (Exception e) {
            detail.setStatus(Constants.FAILED);
            log.error("Error during CORSPostFilter", e);
        } finally {

            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime);

            detail.setTimeInMillisRun(duration);
            detail.setName(this.getClass().getSimpleName());
            TraceContextHolder.getInstance().getActualTrace().addFilter(detail);
        }

        return null;
    }

    private void process() {
        RequestContext requestContext = RequestContext.getCurrentContext();

        Map<String, String> cors = (Map<String, String>) requestContext.get("CORSApply");
        corsInterceptorService.executeCorsPostFilter(requestContext, cors);
    }
}
