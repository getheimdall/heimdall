
package br.com.conductor.heimdall.gateway.service;

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

import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.util.ConstantsInterceptors;
import br.com.conductor.heimdall.core.util.DigestUtils;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.twsoftware.alfred.object.Objeto;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static br.com.conductor.heimdall.core.util.Constants.INTERRUPT;

/**
 * Provides methods to validade AccessToken in interceptor.
 *
 * @author Filipe Germano
 *
 */
@Service
public class SecurityService {
     
     @Autowired
     private AccessTokenRepository accessTokenRepository;

     @Autowired
     private AppRepository appRepository;
     
     /**
      * Method responsible for validating access_token in interceptor
      * 
      * @param ctx 
      * @param apiId
      * @param clientId
      * @param accessToken
      */
     public void validadeAccessToken(RequestContext ctx, Long apiId, String clientId, String accessToken) {
                    
          final String ACCESS_TOKEN = "Access Token";
          
          TraceContextHolder.getInstance().getActualTrace().setAccessToken(DigestUtils.digestMD5(accessToken));
          
          if (Objeto.notBlank(accessToken) && Objeto.notBlank(clientId)) {
                    
               AccessToken token = accessTokenRepository.findAccessTokenActive(accessToken);               
               if (Objeto.notBlank(token)) {
                    
                    List<Plan> plans = token.getApp().getPlans();
                    Plan plan = plans.stream().filter(p -> apiId.equals(p.getApi().getId())).findFirst().orElse(null);
                    if (Objeto.notBlank(plan)) {
                         
                         String cId = token.getApp().getClientId();
                         
                         if (Objeto.notBlank(cId) && clientId.equals(cId)) {
                              
                              TraceContextHolder.getInstance().getActualTrace().setApp(token.getApp().getName());
                         } else {

                              buildResponse(ctx, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
                         }
                    } else {

                         buildResponse(ctx, ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                    }

               } else {

                    buildResponse(ctx, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
               }
          } else {

               buildResponse(ctx, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
          }
     }
     
     /**
      * Method responsible for validating client_id in interceptor
      *
      * @param ctx      {@link RequestContext}
      * @param apiId    The apiId
      * @param clientId ClientId to be validated
      */
     public void validateClientId(RequestContext ctx, Long apiId, String clientId) {

          final String CLIENT_ID = "Client Id";
          
          if (Objects.nonNull(clientId)) {
               
               TraceContextHolder.getInstance().getActualTrace().setClientId(DigestUtils.digestMD5(clientId));
               App app = appRepository.findByClientId(clientId);
               if (Objects.isNull(app)) {

                    buildResponse(ctx, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, CLIENT_ID));
               } else {

                    Plan plan = app.getPlans().stream().filter(p -> apiId.equals(p.getApi().getId())).findFirst().orElse(null);
                    if (Objects.isNull(plan)) {

                         buildResponse(ctx, ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                    } else {

                    	 TraceContextHolder.getInstance().getActualTrace().setApp(app.getName());
                         TraceContextHolder.getInstance().getActualTrace().setAppDeveloper(app.getDeveloper().getEmail());
                    }
               }
          } else {

               buildResponse(ctx, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, CLIENT_ID));
          }
     }

     private void buildResponse(RequestContext ctx, String message) {

          ctx.setSendZuulResponse(false);
          ctx.put(INTERRUPT, true);
          ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
          ctx.setResponseBody(message);

     }

}
