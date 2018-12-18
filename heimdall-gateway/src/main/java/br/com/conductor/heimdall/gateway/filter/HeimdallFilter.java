
package br.com.conductor.heimdall.gateway.filter;

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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import br.com.conductor.heimdall.core.util.Constants;
import br.com.conductor.heimdall.gateway.trace.FilterDetail;
import br.com.conductor.heimdall.gateway.trace.StackTraceImpl;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;

/**
 * HeimdallFilter is a extension of a {@link ZuulFilter}.
 *
 * @author Filipe Germano
 * @author Marcos Filho
 *
 */
@Component
public abstract class HeimdallFilter extends ZuulFilter {

     public abstract boolean should();

     private FilterDetail detail = new FilterDetail();
     
     @Override
     public boolean shouldFilter() {
          long startTime = System.currentTimeMillis();
          boolean should = true;
          
          RequestContext ctx = RequestContext.getCurrentContext();
          if (ctx == null || ctx.getRequest() == null) {
               should =  false;
          }
          
          if (!ctx.sendZuulResponse()) {
               return false;
          }
          
          boolean validateExecution = should && validateExecution();
          should = should && should();
          
          long endTime = System.currentTimeMillis();
          long duration = (endTime - startTime);
          
          detail.setTimeInMillisShould(duration);
          return validateExecution && should;
     }

     private boolean validateExecution() {

          RequestContext ctx = RequestContext.getCurrentContext();
          String uri = ctx.getRequest().getRequestURI();

          return !(uri.equalsIgnoreCase("/") || uri.startsWith("/manager"));

     }

     @Override
     public Object run() {

          long startTime = System.currentTimeMillis();
          try {
               execute();
               detail.setStatus(Constants.SUCCESS);
          } catch (Throwable e) {
               detail.setStatus(Constants.FAILED);
               detail.setStackTrace(new StackTraceImpl(e.getClass().getName(), e.getMessage(), ExceptionUtils.getStackTrace(e)));
          } finally {
               long endTime = System.currentTimeMillis();

               long duration = (endTime - startTime);

               detail.setName(getName());
               detail.setTimeInMillisRun(duration);
               TraceContextHolder.getInstance().getActualTrace().addFilter(detail);
          }
          return null;
     }     

     public abstract void execute() throws Throwable;
     public abstract String getName();

}
