/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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
package br.com.conductor.heimdall.gateway.service;

import br.com.conductor.heimdall.middleware.spec.Helper;
import com.netflix.zuul.context.RequestContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Mock Interceptor service provides a simple response to show the user.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class MockInterceptorService {
	
	@Autowired
    private Helper helper;

    /**
     * Creates a response to the user with the body content.
     *
     * @param status HttpStatus to be sent back
     * @param body   Response body
     */
    public void execute(int status, String body) {

        RequestContext ctx = RequestContext.getCurrentContext();

        if (isJson(body)) {
            helper.call().response().header().add("Content-Type", "application/json");
        } else {

            helper.call().response().header().add("Content-Type", "text/plain");
        }

        helper.call().response().setBody(body);

        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(status);

    }

    private boolean isJson(String string) {
        boolean valid;
        try {

            new JSONObject(string);
            valid = true;
        } catch (JSONException e) {

            try {
                new JSONArray(string);
                valid = true;
            } catch (JSONException ex1) {
                valid = false;
            }
        }

        return valid;
    }

}
