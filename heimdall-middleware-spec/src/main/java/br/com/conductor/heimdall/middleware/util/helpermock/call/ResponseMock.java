
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
import br.com.conductor.heimdall.middleware.spec.Response;

import java.util.Arrays;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class ResponseMock implements Response {

    private Integer status;
    private String body;

    @Override
    public Header header() {
        return new HeaderMock();
    }

    @Override
    public Integer getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(Integer status) {
        this.status = status;
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
    public void setBody(byte[] body) {
        this.body = Arrays.toString(body);
    }

    @Override
    public void setBody(byte[] body, boolean gzip) {
        this.body = Arrays.toString(body);
    }
}
