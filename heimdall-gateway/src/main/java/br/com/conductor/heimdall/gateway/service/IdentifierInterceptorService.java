package br.com.conductor.heimdall.gateway.service;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static br.com.conductor.heimdall.core.util.ConstantsInterceptors.IDENTIFIER_ID;

@Service
public class IdentifierInterceptorService {

    public void execute() {
        String uid = UUID.randomUUID().toString();

        RequestContext context = RequestContext.getCurrentContext();
        context.addZuulRequestHeader(IDENTIFIER_ID, uid);
    }
}
