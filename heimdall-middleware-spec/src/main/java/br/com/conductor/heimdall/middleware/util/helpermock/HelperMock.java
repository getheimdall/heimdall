
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

import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import br.com.conductor.heimdall.middleware.spec.Call;
import br.com.conductor.heimdall.middleware.spec.DB;
import br.com.conductor.heimdall.middleware.spec.DBMongo;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.conductor.heimdall.middleware.spec.Json;
import br.com.conductor.heimdall.middleware.spec.Xml;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class HelperMock implements Helper {

    @Override
    public ApiResponse apiResponse() {
        return new ApiResponseMockImpl();
    }

    @Override
    public Call call() {
        return new CallMockImpl();
    }

    @Override
    public DB db(String databaseName) {
        return new DBMockImpl(databaseName);
    }

    @Override
    public DBMongo dbMongo(String databaseName) {
        return (DBMongo) db(databaseName);
    }

    @Override
    public Http http() {
        return new HttpMockImpl();
    }

    @Override
    public Json json() {
        return new JsonMockImpl();
    }

    @Override
    public Xml xml() {
        return new XmlMockImpl();
    }

	@Override
	public void httpHandler(boolean useHandler) {
		
	}
}
