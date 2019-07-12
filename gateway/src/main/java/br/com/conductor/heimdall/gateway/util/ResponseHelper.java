/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License")
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
package br.com.conductor.heimdall.gateway.util;

import br.com.conductor.heimdall.core.util.ContentTypeUtils;
import com.netflix.zuul.context.RequestContext;
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

/**
 * Provides static methods to properly handle request body and headers
 *
 * @author Marcelo Aguiar Rodrigues
 */
public class ResponseHelper {

    private ResponseHelper() {}

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

    public static String getResponseBody(RequestContext context, Map<String, String> headers) throws Throwable {
        String content = headers.get(HttpHeaders.CONTENT_TYPE);
        String response = null;

        // if the content type is not defined by api server then permit to read the body. Prevent NPE
        if (content == null || content.isEmpty()) content = "";

        String[] types = content.split(";");

        if (!ContentTypeUtils.belongsToBlackList(types)) {

            try (InputStream stream = context.getResponseDataStream()) {

                response = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);

            if (response.isEmpty() && context.getResponseBody() != null) {

                response = context.getResponseBody();
            }

            if (Objects.isNull(response) || response.isEmpty()) {

                response = "";
            }
            context.setResponseDataStream(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));
            }
        }
        return response;
    }
}
