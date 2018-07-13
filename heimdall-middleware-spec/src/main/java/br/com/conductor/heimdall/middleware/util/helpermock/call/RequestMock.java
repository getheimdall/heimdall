
package br.com.conductor.heimdall.middleware.util.helpermock.call;

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

import br.com.conductor.heimdall.middleware.spec.Header;
import br.com.conductor.heimdall.middleware.spec.Query;
import br.com.conductor.heimdall.middleware.spec.Request;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class RequestMock implements Request {

    private String body;
    private String url;

    @Override
    public Header header() {
        return new HeaderMock();
    }

    @Override
    public Query query() {
        return new QueryMock();
    }

    @Override
    public String getBody() {
        return this.body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void setUrl(String routeUrl) {
        this.url = routeUrl;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String pathParam(String name) {
        // TODO
        return null;
    }

    @Override
    public String getAppName() {
        // TODO
        return null;
    }

    @Override
    public void setSendResponse(boolean value) {
        // TODO
    }
}

