
package br.com.conductor.heimdall.middleware.util.helpermock;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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

import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.conductor.heimdall.middleware.spec.Json;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class HttpMockImpl implements Http {

    private Json json;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String url;
    private String body;

    public HttpMockImpl() {
        this.json = new JsonMockImpl();
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    @Override
    public Http header(String name, String value) {

        this.headers.put(name, value);

        return this;
    }

    @Override
    public Http header(Map<String, String> params) {

        params.forEach((k, v) -> {
            if (v != null) {
                this.headers.put(k, v);
            }
        });

        return this;
    }

    @Override
    public Http url(String url) {
        this.url = url;
        return this;
    }

    @Override
    public Http queryParam(String name, String value) {
        this.queryParams.put(name, value);
        return this;
    }

    @Override
    public Http body(Map<String, Object> params) {
        this.body = json.parse(params);
        return this;
    }

    @Override
    public Http body(String params) {
        this.body = json.parse(params);
        return this;
    }

    @Override
    public ApiResponse sendGet() {

        ApiResponse response = new ApiResponseMockImpl();

        response.setHeaders(headers);
        response.setBody("{ \"response\" : \"response body\"");
        response.setStatus(200);

        return response;
    }

    @Override
    public ApiResponse sendPost() {
        return this.sendGet();
    }

    @Override
    public ApiResponse sendPut() {
        return this.sendGet();
    }

    @Override
    public ApiResponse sendDelete() {
        return this.sendGet();
    }

    /**
     * Method responsible for testing request calls using the HTTP PATCH verb.
     */
    @Override
    public ApiResponse sendPatch() {
        ApiResponse response = new ApiResponseMockImpl();
        response.setStatus(HttpStatus.NO_CONTENT.value());
        return response;
    }

    @Override
    public RestTemplate clientProvider(RestTemplate restTemplate) {
        return new RestTemplate();
    }
}
