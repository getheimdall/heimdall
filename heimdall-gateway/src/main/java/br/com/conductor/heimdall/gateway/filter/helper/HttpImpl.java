
package br.com.conductor.heimdall.gateway.filter.helper;

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

import br.com.conductor.heimdall.gateway.filter.helper.http.HeimdallResponseErrorHandler;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.conductor.heimdall.middleware.spec.Json;
import br.com.twsoftware.alfred.object.Objeto;
import com.google.common.collect.Lists;
import com.netflix.zuul.context.RequestContext;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static br.com.conductor.heimdall.core.util.ConstantsInterceptors.IDENTIFIER_ID;

/**
 * Implementation of the {@link Http} interface.
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
public class HttpImpl implements Http {

    private Json json = new JsonImpl();

    private HttpHeaders headers = new HttpHeaders();

    private UriComponentsBuilder uriComponentsBuilder;

    private HttpEntity<String> requestBody;

    private String body;

    private MultiValueMap<String, String> formData;

    private RestTemplate restTemplate;

    private boolean enableHandler;

    public HttpImpl() {
        this.enableHandler = false;
    }

    public HttpImpl(boolean enableHandler) {
        this.enableHandler = enableHandler;
    }

    @Override
    public HttpImpl header(String name, String value) {

        if (Objeto.notBlank(value)) {

            headers.add(name, value);
        }

        return this;
    }

    @Override
    public HttpImpl header(Map<String, String> params) {

        params.forEach((key, value) -> {
            if (value != null)
                headers.add(key, value);
        });

        return this;
    }

    @Override
    public HttpImpl url(String url) {

        uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);

        return this;
    }

    @Override
    public HttpImpl queryParam(String name, String value) {

        if (Objects.nonNull(value)) {

            uriComponentsBuilder.queryParam(name, value);
        }

        return this;
    }

    @Override
    public HttpImpl body(Map<String, Object> params) {

        if (headers.containsKey("Content-Type")
                && headers.get("Content-Type").get(0).equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
            formData = new LinkedMultiValueMap<>();
            params.forEach((key, value) -> {
                List<String> values = Lists.newArrayList(value.toString());
                formData.put(key, values);
            });
        } else {

            body = json.parse(params);
        }

        return this;
    }

    @Override
    public HttpImpl body(String params) {

        body = json.parse(params);

        return this;

    }

    @Override
    public ApiResponseImpl sendGet() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;

        if (headers.isEmpty()) {

            entity = rest().getForEntity(uriComponentsBuilder.build().encode().toUri(), String.class);
        } else {

            entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        }

        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(entity.getHeaders().toSingleValueMap());
        apiResponse.setBody(entity.getBody());
        apiResponse.setStatus(entity.getStatusCodeValue());

        return apiResponse;
    }

    @Override
    public ApiResponseImpl sendPost() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;
        if (headers.isEmpty()) {

            requestBody = new HttpEntity<>(body);
            entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, requestBody, String.class);
        } else {

            if (Objeto.notBlank(formData)) {

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, request, String.class);
            } else {

                requestBody = new HttpEntity<>(body, headers);
                entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, requestBody, String.class);
            }

            requestBody = new HttpEntity<>(body, headers);
        }
        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(entity.getHeaders().toSingleValueMap());

        apiResponse.setBody(entity.getBody());

        apiResponse.setStatus(entity.getStatusCodeValue());

        return apiResponse;
    }

    @Override
    public ApiResponseImpl sendPut() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;
        if (headers.isEmpty()) {

            requestBody = new HttpEntity<>(body);
            entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PUT, requestBody, String.class);
        } else {

            if (Objeto.notBlank(formData)) {

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PUT, request, String.class);
            } else {

                requestBody = new HttpEntity<>(body, headers);
                entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PUT, requestBody, String.class);
            }
        }
        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(entity.getHeaders().toSingleValueMap());
        apiResponse.setBody(entity.getBody());
        apiResponse.setStatus(entity.getStatusCodeValue());

        return apiResponse;
    }

    @Override
    public ApiResponseImpl sendDelete() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;

        if (headers.isEmpty()) {
            entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.DELETE, null,
                    String.class);
        } else {
            entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.DELETE,
                    new HttpEntity<>(headers), String.class);
        }

        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(entity.getHeaders().toSingleValueMap());
        apiResponse.setBody(entity.getBody());
        apiResponse.setStatus(entity.getStatusCodeValue());

        return apiResponse;
    }

    @Override
    public ApiResponseImpl sendPatch() {

        ResponseEntity<String> entity;

        if (headers.isEmpty()) {
            requestBody = new HttpEntity<>(body);
            entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PATCH, requestBody,
                    String.class);
        } else {
            if (Objeto.notBlank(formData)) {
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PATCH, request,
                        String.class);
            } else {
                requestBody = new HttpEntity<>(body, headers);
                entity = rest().exchange(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PATCH, requestBody,
                        String.class);
            }
        }

        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(entity.getHeaders().toSingleValueMap());
        apiResponse.setBody(entity.getBody());
        apiResponse.setStatus(entity.getStatusCodeValue());

        return apiResponse;
    }

    @Override
    public RestTemplate clientProvider(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        return this.restTemplate;
    }

    private RestTemplate rest() {
        if (this.restTemplate == null) {
            this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        }

        if (enableHandler) {
            this.restTemplate.setErrorHandler(new HeimdallResponseErrorHandler());
        }
        return this.restTemplate;
    }

    /*
     * Forwards the Identifier from the IdentifierInterceptor if it is set
     */
    private void setUIDFromInterceptor() {
        RequestContext context = RequestContext.getCurrentContext();

        if (context.getZuulRequestHeaders().get(IDENTIFIER_ID) != null) {
            headers.add(IDENTIFIER_ID, context.getZuulRequestHeaders().get(IDENTIFIER_ID));
        }
    }

}