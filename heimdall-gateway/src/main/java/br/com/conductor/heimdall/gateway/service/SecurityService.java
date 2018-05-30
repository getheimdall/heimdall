package br.com.conductor.heimdall.gateway.service;

import static br.com.conductor.heimdall.core.util.Constants.INTERRUPT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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

import org.springframework.stereotype.Service;

import com.netflix.zuul.context.RequestContext;

import br.com.conductor.heimdall.core.entity.AccessToken;
import br.com.conductor.heimdall.core.entity.App;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.repository.AccessTokenRepository;
import br.com.conductor.heimdall.core.repository.AppRepository;
import br.com.conductor.heimdall.core.util.ConstantsInterceptors;
import br.com.conductor.heimdall.core.util.DigestUtils;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.middleware.spec.Helper;
import br.com.twsoftware.alfred.object.Objeto;

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
      * @param helper
      * @param apiId
      * @param clientId
      * @param accessToken
      */
     public void validadeAccessToken(RequestContext ctx, Helper helper, Long apiId, String clientId, String accessToken) {
                    
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
                              
                              buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
                         }
                    } else {
                         
                         buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                    }
                    
               } else {
                    
                    buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
               }
          } else {
               
               buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, ACCESS_TOKEN));
          }
          
     }
     
     /**
      * Method responsible for validating client_id in interceptor
      * 
      * @param ctx
      * @param helper
      * @param apiId
      * @param clientId
      */
     public void validadeClientId(RequestContext ctx, Helper helper, Long apiId, String clientId) {
          
          final String CLIENT_ID = "Client Id";
          
          if (Objeto.notBlank(clientId)) {
               
               TraceContextHolder.getInstance().getActualTrace().setClientId(DigestUtils.digestMD5(clientId));
               App app = appRepository.findByClientId(clientId);
               
               Plan plan = app.getPlans().stream().filter(p -> apiId.equals(p.getApi().getId())).findFirst().orElse(null);
               
               if (Objeto.isBlank(plan)) {

                    buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, ConstantsInterceptors.GLOBAL_ACCESS_NOT_ALLOWED_API);
                    
               } else if (Objeto.isBlank(app)) {

                    buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, CLIENT_ID));
               } else {
                    
                    TraceContextHolder.getInstance().getActualTrace().setAppDeveloper(app.getDeveloper().getEmail());
               }
          } else {

               buildResponse(ctx, HttpStatus.UNAUTHORIZED.value(), true, String.format(ConstantsInterceptors.GLOBAL_CLIENT_ID_OR_ACESS_TOKEN_NOT_FOUND, CLIENT_ID));
          }
          
     }
     
     private void buildResponse(RequestContext ctx, Integer status, Boolean interrupt, String message) {
          
          ctx.setSendZuulResponse(!interrupt);
          ctx.put(INTERRUPT, interrupt);
          ctx.setResponseStatusCode(status);
          ctx.setResponseBody(message);
          
     }

}
