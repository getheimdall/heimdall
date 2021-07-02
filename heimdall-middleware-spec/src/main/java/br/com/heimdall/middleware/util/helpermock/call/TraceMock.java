
package br.com.heimdall.middleware.util.helpermock.call;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
 * ========================================================================
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

import br.com.heimdall.middleware.spec.StackTrace;
import br.com.heimdall.middleware.spec.Trace;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class TraceMock implements Trace {

    private String trace;
    private StackTrace stackTrace;

    @Override
    public void addStackTrace(String clazz, String message, String stack) {

        this.stackTrace = new StackTraceMock(clazz, message, stack);
    }

    @Override
    public StackTrace getStackTrace() {
        return this.stackTrace;
    }

    @Override
    public void addTrace(String trace) {
        this.trace = trace;
    }

    @Override
    public void addTrace(String trace, Object object) {
        this.trace = trace;
    }

}
