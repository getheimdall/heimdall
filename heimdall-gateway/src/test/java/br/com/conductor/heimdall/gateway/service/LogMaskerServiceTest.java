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
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.gateway.trace.RequestResponseParser;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

@RunWith(MockitoJUnitRunner.class)
public class LogMaskerServiceTest {

    @InjectMocks
    private LogMaskerService logMaskerService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        TraceContextHolder.getInstance().init(true, "developer", new MockHttpServletRequest(), false, false);

        Map<String, String> headers = new HashMap<>();
        headers.put("connection", "Keep-alive");
        headers.put("host", "http://another-host.com");
        headers.put("Content-Type", "application/json;utf-8");
        headers.put("simpleHeader", "someInformation");

        String body = "{\"body\":\"this is a simple body\"}";
        String uri = "http://simpleUri.com";

        RequestResponseParser request = new RequestResponseParser();
        RequestResponseParser response = new RequestResponseParser();

        request.setHeaders(headers);
        request.setUri(uri);
        request.setBody(body);

        response.setHeaders(headers);
        response.setUri(uri);
        response.setBody(body);

        TraceContextHolder.getInstance().getActualTrace().setRequest(request);
        TraceContextHolder.getInstance().getActualTrace().setResponse(response);
    }

    @After
    public void after() {
        TraceContextHolder.getInstance().unset();
    }

    @Test
    public void requestDeleteAll() {

        logMaskerService.execute(PRE_TYPE, true, true, true, new ArrayList<>());

        RequestResponseParser request = TraceContextHolder.getInstance().getActualTrace().getRequest();

        assertNull(request.getBody());
        assertNull(request.getUri());
        assertNull(request.getHeaders());
    }

    @Test
    public void requestDeleteBody() {

        logMaskerService.execute(PRE_TYPE, true, false, false, new ArrayList<>());

        RequestResponseParser request = TraceContextHolder.getInstance().getActualTrace().getRequest();

        assertNull(request.getBody());
        assertNotNull(request.getUri());
        assertNotNull(request.getHeaders());
    }

    @Test
    public void requestDeleteUri() {

        logMaskerService.execute(PRE_TYPE, false, true, false, new ArrayList<>());

        RequestResponseParser request = TraceContextHolder.getInstance().getActualTrace().getRequest();

        assertNotNull(request.getBody());
        assertNull(request.getUri());
        assertNotNull(request.getHeaders());
    }

    @Test
    public void requestDeleteAllHeaders() {

        logMaskerService.execute(PRE_TYPE, false, false, true, new ArrayList<>());

        RequestResponseParser request = TraceContextHolder.getInstance().getActualTrace().getRequest();

        assertNotNull(request.getBody());
        assertNotNull(request.getUri());
        assertNull(request.getHeaders());
    }

    @Test
    public void requestDeleteSomeHeaders() {

        List<String> ignoredHeaders = Arrays.asList("simpleHeader", "host");

        logMaskerService.execute(PRE_TYPE, false, false, true, ignoredHeaders);

        RequestResponseParser request = TraceContextHolder.getInstance().getActualTrace().getRequest();

        assertNotNull(request.getBody());
        assertNotNull(request.getUri());
        assertNotNull(request.getHeaders());
        assertNull(request.getHeaders().get("simpleHeader"));
        assertNull(request.getHeaders().get("host"));
    }

    @Test
    public void requestDeleteAbsentHeaders() {

        List<String> ignoredHeaders = Arrays.asList("simpleHeader", "host", "someOtherHeader");

        logMaskerService.execute(PRE_TYPE, false, false, true, ignoredHeaders);

        RequestResponseParser request = TraceContextHolder.getInstance().getActualTrace().getRequest();

        assertNotNull(request.getBody());
        assertNotNull(request.getUri());
        assertNotNull(request.getHeaders());
        assertNull(request.getHeaders().get("simpleHeader"));
        assertNull(request.getHeaders().get("host"));
        assertNull(request.getHeaders().get("someOtherHeader"));
    }

    @Test
    public void responseDeleteAll() {

        logMaskerService.execute(POST_TYPE, true, true, true, new ArrayList<>());

        RequestResponseParser response = TraceContextHolder.getInstance().getActualTrace().getResponse();

        assertNull(response.getBody());
        assertNull(response.getUri());
        assertNull(response.getHeaders());
    }

    @Test
    public void responseDeleteBody() {

        logMaskerService.execute(POST_TYPE, true, false, false, new ArrayList<>());

        RequestResponseParser response = TraceContextHolder.getInstance().getActualTrace().getResponse();

        assertNull(response.getBody());
        assertNotNull(response.getUri());
        assertNotNull(response.getHeaders());
    }

    @Test
    public void responseDeleteUri() {

        logMaskerService.execute(POST_TYPE, false, true, false, new ArrayList<>());

        RequestResponseParser response = TraceContextHolder.getInstance().getActualTrace().getResponse();

        assertNotNull(response.getBody());
        assertNull(response.getUri());
        assertNotNull(response.getHeaders());
    }

    @Test
    public void responseDeleteAllHeaders() {

        logMaskerService.execute(POST_TYPE, false, false, true, new ArrayList<>());

        RequestResponseParser response = TraceContextHolder.getInstance().getActualTrace().getResponse();

        assertNotNull(response.getBody());
        assertNotNull(response.getUri());
        assertNull(response.getHeaders());
    }

    @Test
    public void responseDeleteSomeHeaders() {

        List<String> ignoredHeaders = Arrays.asList("simpleHeader", "host");

        logMaskerService.execute(POST_TYPE, false, false, true, ignoredHeaders);

        RequestResponseParser response = TraceContextHolder.getInstance().getActualTrace().getResponse();

        assertNotNull(response.getBody());
        assertNotNull(response.getUri());
        assertNotNull(response.getHeaders());
        assertNull(response.getHeaders().get("simpleHeader"));
        assertNull(response.getHeaders().get("host"));
    }

    @Test
    public void responseDeleteAbsentHeaders() {

        List<String> ignoredHeaders = Arrays.asList("simpleHeader", "host", "someOtherHeader");

        logMaskerService.execute(POST_TYPE, false, false, true, ignoredHeaders);

        RequestResponseParser response = TraceContextHolder.getInstance().getActualTrace().getResponse();

        assertNotNull(response.getBody());
        assertNotNull(response.getUri());
        assertNotNull(response.getHeaders());
        assertNull(response.getHeaders().get("simpleHeader"));
        assertNull(response.getHeaders().get("host"));
        assertNull(response.getHeaders().get("someOtherHeader"));
    }
}
