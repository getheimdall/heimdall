
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

import java.util.HashMap;
import java.util.Map;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class HeaderMock implements Header {

    private Map<String, String> headers;

    HeaderMock() {
        this.headers = new HashMap<>();
    }

    @Override
    public Map<String, String> getAll() {
        return this.headers;
    }

    @Override
    public String get(String name) {
        return this.headers.get(name);
    }

    @Override
    public void set(String name, String value) {
        this.add(name, value);
    }

    @Override
    public void add(String name, String value) {
        this.headers.put(name, value);
    }

    @Override
    public void addAll(Map<String, String> values) {
        headers.putAll(values);
    }

    @Override
    public void remove(String name) {
        this.headers.remove(name);
    }

    @Override
    public String getMethod() {
        return "";
    }


}