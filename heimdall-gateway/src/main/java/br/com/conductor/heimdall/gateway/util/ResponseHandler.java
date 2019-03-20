package br.com.conductor.heimdall.gateway.util;

import br.com.conductor.heimdall.core.util.ContentTypeUtils;
import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.twsoftware.alfred.object.Objeto;
import com.netflix.zuul.context.RequestContext;
import lombok.Cleanup;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResponseHandler {

    public static Map<String, String> getResponseHeaders(RequestContext context) {

        Map<String, String> headers = new HashMap<>();

        final HttpServletResponse response = context.getResponse();

        context.getZuulResponseHeaders().forEach(pair -> headers.put(pair.first(), pair.second()));

        final Collection<String> headerNames = response.getHeaderNames();

        headerNames.forEach(s -> headers.putIfAbsent(s, response.getHeader(s)));

        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            headers.put(HttpHeaders.CONTENT_TYPE, context.getResponse().getContentType());
        }

        headers.remove("X-Application-Context");

        return headers;
    }

    public static String getResponseBody(RequestContext context, Map<String, String> headers, Helper helper) throws Throwable {
        String content = headers.get(HttpHeaders.CONTENT_TYPE);
        String response = null;

        // if the content type is not defined by api server then permit to read the body. Prevent NPE
        if (Objeto.isBlank(content)) content = "";

        String[] types = content.split(";");

        if (!ContentTypeUtils.belongsToBlackList(types)) {
            @Cleanup
            InputStream stream = context.getResponseDataStream();

            response = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);

            if (response.isEmpty() && helper.call().response().getBody() != null) {

                response = helper.call().response().getBody();
            }

            if (Objects.isNull(response) || response.isEmpty()) {

                response = "";
            }
            context.setResponseDataStream(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));

        }
        return response;
    }
}
