
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

import br.com.heimdall.middleware.spec.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class EnvironmentMock implements Environment {

    private Map<String, String> currentVariables;

    public EnvironmentMock() {
        this.currentVariables = new HashMap<>();
    }

    @Override
    public Map<String, String> getVariables() {

        return currentVariables;
    }

    @Override
    public String getVariable(String key) {

        return currentVariables.get(key);
    }

    /**
     * Use this method to set the mock environment variables
     *
     * @param currentVariables Environment variables Map
     */
    public void setCurrentVariables(Map<String, String> currentVariables) {
        this.currentVariables = currentVariables;
    }
}