
package br.com.conductor.heimdall.gateway.filter.helper;

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

import br.com.conductor.heimdall.middleware.enums.DBType;

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

import br.com.conductor.heimdall.middleware.spec.ApiResponse;
import br.com.conductor.heimdall.middleware.spec.Call;
import br.com.conductor.heimdall.middleware.spec.DB;
import br.com.conductor.heimdall.middleware.spec.DBMongo;
import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.conductor.heimdall.middleware.spec.Http;
import br.com.conductor.heimdall.middleware.spec.Json;
import br.com.conductor.heimdall.middleware.spec.Xml;

/**
 * Implementation of the {@link Helper} interface.
 *
 * @author Filipe Germano
 *
 */
public class HelperImpl implements Helper {

	private boolean enableHandler;

	public HelperImpl() {
		enableHandler = false;
	}

	@Override
	public ApiResponse apiResponse() {

		ApiResponse apiResponse = new ApiResponseImpl();
		return apiResponse;
	}

	@Override
	public Call call() {

		Call call = new CallImpl();
		return call;
	}

	@Override
	public DB db(String databaseName) {

		return db(databaseName, DBType.MONGODB);
	}

	private DB db(String databaseName, DBType type) {

		switch (type) {
		case MONGODB:
			return new DBMongoImpl(databaseName);
		default:
			return new DBMongoImpl(databaseName);
		}
	}

	@Override
	public DBMongo dbMongo(String databaseName) {

		return (DBMongo) db(databaseName, DBType.MONGODB);
	}

	@Override
	public Http http() {

		Http http = new HttpImpl(enableHandler);
		return http;
	}

	@Override
	public Json json() {

		Json json = new JsonImpl();
		return json;
	}

	@Override
	public Xml xml() {

		return new XmlImpl();
	}

	@Override
	public void httpHandler(boolean useHandler) {
		this.enableHandler = useHandler;
	}

}
