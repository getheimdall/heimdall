
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

import br.com.heimdall.middleware.spec.Info;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class InfoMock implements Info {

    private String app;
    private String apiName;
    private Long apiId;
    private String appDeveloper;
    private String method;
    private String clientId;
    private String accessToken;
    private String pattern;
    private Long operationId;
    private String profile;
    private Long resourceId;
    private String url;
    private String requestURI;

    @Override
    public String appName() {
        return this.app;
    }

    @Override
    public String apiName() {
        return this.apiName;
    }

    @Override
    public Long apiId() {
        return this.apiId;
    }

    @Override
    public String developer() {
        return this.appDeveloper;
    }

    @Override
    public String method() {
        return this.method;
    }

    @Override
    public String clientId() {
        return this.clientId;
    }

    @Override
    public String accessToken() {
        return this.accessToken;
    }

    @Override
    public String pattern() {
        return this.pattern;
    }

    @Override
    public Long operationId() {
        return this.operationId;
    }

    @Override
    public String profile() {
        return this.profile;
    }

    @Override
    public Long resourceId() {
        return this.resourceId;
    }

    @Override
    public String url() {
        return this.url;
    }

    @Override
    public String requestURI() {
        return this.requestURI;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param app app
     */
    public void setApp(String app) {
        this.app = app;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param apiName api name
     */
    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param apiId api id
     */
    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param appDeveloper app Developer
     */
    public void setAppDeveloper(String appDeveloper) {
        this.appDeveloper = appDeveloper;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param method method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param clientId clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param accessToken access Token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param pattern pattern
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param operationId operation id
     */
    public void setOperationId(Long operationId) {
        this.operationId = operationId;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param profile profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param resourceId resource id
     */
    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Set method designed for mock purposes
     *
     * @param requestURI request URI
     */
    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }
}
