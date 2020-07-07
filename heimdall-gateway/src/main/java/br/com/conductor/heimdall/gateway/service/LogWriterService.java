package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.core.trace.RequestResponseParser;
import br.com.conductor.heimdall.core.trace.TraceContextHolder;
import br.com.conductor.heimdall.core.util.DigestUtils;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import br.com.conductor.heimdall.gateway.util.ResponseHelper;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LogWriterService {


    public void execute(String filterType,
                        Boolean writeBody,
                        Boolean writeHeaders,
                        List<String> requiredHeaders) throws Throwable {

        if (!writeBody && !writeHeaders) return;
        
        if(filterType.equals("pre")){
            writeRequest(writeBody, writeHeaders, requiredHeaders);
        }else if(filterType.equals("post")){
            writeResponse(writeBody,  writeHeaders, requiredHeaders);
        }
    }

    private void writeResponse(Boolean writeBody,
                               Boolean writeHeaders,
                               List<String> requiredHeaders) throws Throwable {

        RequestContext ctx = RequestContext.getCurrentContext();

        RequestResponseParser r = new RequestResponseParser();

        Map<String, String> headers = ResponseHelper.getResponseHeaders(ctx);
        if (writeHeaders) {
            if (requiredHeaders.isEmpty()) {
                r.setHeaders(headers);
            } else {
                Map<String, String> map = r.getHeaders().entrySet().stream()
                        .filter(entry -> requiredHeaders.contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                r.setHeaders(map);
            }
        }

        if (writeBody) {
            String body = ResponseHelper.getResponseBody(ctx, headers);
            r.setBody(body);
        }

        TraceContextHolder.getInstance().getActualTrace().setResponse(r);
    }


    private void writeRequest(Boolean writeBody,
                             Boolean writeHeaders,
                             List<String> requiredHeaders) {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        RequestResponseParser r = new RequestResponseParser();

        if (writeHeaders) {
            final Map<String, String> requestHeaders = getRequestHeaders(request);
            if (requiredHeaders.isEmpty()) {
                r.setHeaders(requestHeaders);
            } else {
                Map<String, String> map = requestHeaders.entrySet().stream()
                        .filter(entry -> requiredHeaders.contains(entry.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                r.setHeaders(map);
            }
        }


        if (writeBody) {
            r.setBody(getRequestBody(ctx));
        }

        TraceContextHolder.getInstance().getActualTrace().setRequest(r);
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {

        HashMap<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {

            String key = headerNames.nextElement();

            String value;
            if (ConstantsContext.ACCESS_TOKEN.equals(key) || ConstantsContext.CLIENT_ID.equals(key)) {

                value = DigestUtils.digestMD5(request.getHeader(key));
            } else {

                value = request.getHeader(key);
            }

            map.put(key, value);
        }

        return map;
    }

    private String getRequestBody(RequestContext context) {
        try (InputStream in = (InputStream) context.get("requestEntity")) {

            String bodyText;
            if (in == null) {
                bodyText = StreamUtils.copyToString(context.getRequest().getInputStream(), StandardCharsets.UTF_8);
            } else {
                bodyText = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            }

            return bodyText;
        } catch (Exception e) {

            return null;
        }
    }
}
