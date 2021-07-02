
package br.com.heimdall.middleware.util.helpermock;

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

import br.com.heimdall.middleware.spec.*;
import br.com.heimdall.middleware.util.helpermock.call.*;
import br.com.heimdall.middleware.spec.Call;
import br.com.heimdall.middleware.spec.Environment;
import br.com.heimdall.middleware.spec.Info;
import br.com.heimdall.middleware.spec.Request;
import br.com.heimdall.middleware.spec.Response;
import br.com.heimdall.middleware.spec.Trace;
import br.com.heimdall.middleware.util.helpermock.call.EnvironmentMock;
import br.com.heimdall.middleware.util.helpermock.call.InfoMock;
import br.com.heimdall.middleware.util.helpermock.call.RequestMock;
import br.com.heimdall.middleware.util.helpermock.call.ResponseMock;
import br.com.heimdall.middleware.util.helpermock.call.TraceMock;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class CallMockImpl implements Call {

    @Override
    public Request request() {
        return new RequestMock();
    }

    @Override
    public Response response() {
        return new ResponseMock();
    }

    @Override
    public Trace trace() {
        return new TraceMock();
    }

    @Override
    public Environment environment() {
        return new EnvironmentMock();
    }

    @Override
    public Info info() {
        return new InfoMock();
    }
}
