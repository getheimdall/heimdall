
package br.com.heimdall.middleware.util.helpermock.call;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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

import br.com.heimdall.middleware.spec.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class QueryMock implements Query {

    private Map<String, String> queries;

    QueryMock() {
        this.queries = new HashMap<>();
    }

    @Override
    public Map<String, String> getAll() {
        return this.queries;
    }

    @Override
    public String get(String name) {
        return this.queries.get(name);
    }

    @Override
    public void set(String name, String value) {
        this.add(name, value);
    }

    @Override
    public void add(String name, String value) {
        this.queries.put(name, value);
    }

    @Override
    public void remove(String name) {
        this.queries.remove(name);
    }
}