/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.heimdall.gateway.service;

import br.com.heimdall.core.entity.AccessToken;
import br.com.heimdall.core.entity.Plan;
import br.com.heimdall.core.enums.Location;
import br.com.heimdall.core.repository.AccessTokenRepository;
import br.com.heimdall.core.util.ConstantsInterceptors;
import br.com.heimdall.core.util.DigestUtils;
import br.com.heimdall.core.trace.TraceContextHolder;
import br.com.heimdall.gateway.util.ConstantsContext;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.heimdall.core.util.Constants.INTERRUPT;

/**
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class AccessTokenInterceptorService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    /**
     * Validates if a access token originated from {@link Location} is valid
     *
     * @param apiId    Api id
     * @param location {@link Location}
     */
    public void validate(Long apiId, Location location) {

        RequestContext context = RequestContext.getCurrentContext();

        String clientId;
        String accessToken;
        if (Location.HEADER.equals(location)) {
            clientId = context.getZuulRequestHeaders().get(ConstantsContext.CLIENT_ID);
            accessToken = context.getZuulRequestHeaders().get(ConstantsContext.ACCESS_TOKEN);

            if (clientId == null) clientId = context.getRequest().getHeader(ConstantsContext.CLIENT_ID);
            if (accessToken == null) accessToken = context.getRequest().getHeader(ConstantsContext.ACCESS_TOKEN);
        } else {
            clientId = context.getRequest().getParameter(ConstantsContext.CLIENT_ID);
            accessToken = context.getRequest().getParameter(ConstantsContext.ACCESS_TOKEN);
        }

        this.validateAccessToken(apiId, clientId, accessToken);
    }

    /**
     * Method responsible for validating access_token in interceptor
     *
     * @param apiId       Api reference id
     * @param clientId    user client_id
     * @param accessToken access token
     */
    private void validateAccessToken(Long apiId, String clientId, String accessToken) {

        final String ACCESS_TOKEN = "Access Token";

        TraceContextHolder.getInstance().getActualTrace().setAccessToken(DigestUtils.digestMD5(accessToken));

        if (Objects.isNull(clientId) || clientId.isEmpty()) {
            buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, "Client Id"));
            return;
        }

        if (Objects.nonNull(accessToken) && !accessToken.isEmpty()){
            AccessToken token = accessTokenRepository.findAccessTokenActive(accessToken);

            if(Objects.nonNull(token) && Objects.nonNull(token.getApp())){

                List<Plan> plans = token.getApp().getPlans();
                Set<Long> collect = plans.parallelStream().map(plan -> plan.getApi().getId()).collect(Collectors.toSet());
                if (collect.contains(apiId)) {

                    String cId = token.getApp().getClientId();
                    if (clientId.equals(cId)) {
                        TraceContextHolder.getInstance().getActualTrace().setApp(token.getApp().getName());
                    } else {
                        buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
                    }
                } else {
                    buildResponse(ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                }
            } else {
                buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
            }
        }else {
            buildResponse(String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
        }

    }

    private void buildResponse(String message) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setSendZuulResponse(false);
        ctx.put(INTERRUPT, true);
        ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        ctx.setResponseBody(message);
    }
}
