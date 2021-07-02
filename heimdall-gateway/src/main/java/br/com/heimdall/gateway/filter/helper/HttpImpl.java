/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 *  
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
package br.com.heimdall.gateway.filter.helper;

import static br.com.heimdall.core.util.ConstantsInterceptors.IDENTIFIER_ID;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.heimdall.gateway.failsafe.CircuitBreakerManager;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.zuul.context.RequestContext;

import br.com.heimdall.middleware.spec.Http;
import br.com.heimdall.middleware.spec.Json;

/**
 * Implementation of the {@link Http} interface.
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class HttpImpl implements Http {

    private Json json = new JsonImpl();

    private HttpHeaders headers = new HttpHeaders();

    private UriComponentsBuilder uriComponentsBuilder;

    private HttpEntity<String> requestBody;

    private String body;

    private MultiValueMap<String, String> formData;

    private RestTemplate restTemplate;
    
    private MultiValueMap<String, String> queryParams;

    private CircuitBreakerManager circuitBreakerManager;

    private boolean isFailSafeEnabled;

    public HttpImpl(RestTemplate restTemplate, CircuitBreakerManager circuitBreakerManager, boolean isFailSafeEnabled) {
    	this.restTemplate = restTemplate;
        this.circuitBreakerManager = circuitBreakerManager;
        this.isFailSafeEnabled = isFailSafeEnabled;
        this.queryParams = new LinkedMultiValueMap<>();
    }

    @Override
    public HttpImpl header(String name, String value) {

        if (value != null) {

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
            queryParams.add(name, value);
        }

        return this;
    }

    @Override
    public HttpImpl body(Map<String, Object> params) {

        if (headers.containsKey("Content-Type")
                && headers.get("Content-Type").get(0).equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
            formData = new LinkedMultiValueMap<>();
            params.forEach((key, value) -> {
                List<String> values = new ArrayList<>();
                values.add(value.toString());
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
    
        updateQueryParams();
        if (headers.isEmpty()) {
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.GET, new HttpEntity(new HttpHeaders()), String.class);
        } else {
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.GET, new HttpEntity<>(headers), String.class);
        }

        return buildResponse(entity);
    }

    @Override
    public ApiResponseImpl sendPost() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;
    
        updateQueryParams();
        if (headers.isEmpty()) {

            requestBody = new HttpEntity<>(body);
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, requestBody, String.class);
        } else {

            if (formData != null) {

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, request, String.class);
            } else {

                requestBody = new HttpEntity<>(body, headers);
                entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.POST, requestBody, String.class);
            }

            requestBody = new HttpEntity<>(body, headers);
        }

        return buildResponse(entity);
    }

    @Override
    public ApiResponseImpl sendPut() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;
    
        updateQueryParams();
        if (headers.isEmpty()) {

            requestBody = new HttpEntity<>(body);
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PUT, requestBody, String.class);
        } else {

            if (formData != null) {

                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PUT, request, String.class);
            } else {

                requestBody = new HttpEntity<>(body, headers);
                entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PUT, requestBody, String.class);
            }
        }

        return buildResponse(entity);
    }

    @Override
    public ApiResponseImpl sendDelete() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;
    
        updateQueryParams();
        if (headers.isEmpty()) {
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.DELETE, null, String.class);
        } else {
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
        }

        return buildResponse(entity);
    }

    @Override
    public ApiResponseImpl sendPatch() {

        setUIDFromInterceptor();
        ResponseEntity<String> entity;
    
        updateQueryParams();
        if (headers.isEmpty()) {
            requestBody = new HttpEntity<>(body);
            entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PATCH, requestBody, String.class);
        } else {
            if (formData != null) {
                HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
                entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PATCH, request, String.class);
            } else {
                requestBody = new HttpEntity<>(body, headers);
                entity = sendRequest(uriComponentsBuilder.build().encode().toUri(), HttpMethod.PATCH, requestBody, String.class);
            }
        }

        return buildResponse(entity);
    }

    private <T> ResponseEntity<T> sendRequest(URI uri, HttpMethod method, HttpEntity httpEntity, Class<T> responseType) {

        if (isFailSafeEnabled) {

            String url = method.name() + ":" + uri.toString();

            return circuitBreakerManager.failsafe(
                    () -> this.restTemplate.exchange(uri, method, httpEntity, responseType),
                    url
            );
        }

        return this.restTemplate.exchange(uri, method, httpEntity, responseType);

    }

    /**
     * This provider will affect only the next request inside middleware
     */
    @Override
    @Deprecated
    public RestTemplate clientProvider(RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
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

    private void updateQueryParams() {
        if (uriComponentsBuilder != null && queryParams != null && !queryParams.isEmpty()) {
            uriComponentsBuilder.queryParams(queryParams);
        }
    }

    private ApiResponseImpl buildResponse(ResponseEntity<String> entity) {
        ApiResponseImpl apiResponse = new ApiResponseImpl();
        apiResponse.setHeaders(entity.getHeaders().toSingleValueMap());
        apiResponse.setBody(entity.getBody());
        apiResponse.setStatus(entity.getStatusCodeValue());

        return apiResponse;
    }

}