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
package br.com.heimdall.gateway.filter;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import br.com.heimdall.core.util.Constants;
import br.com.heimdall.core.trace.FilterDetail;
import br.com.heimdall.core.trace.TraceContextHolder;

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
          this.detail.clear();
          long startTime = System.currentTimeMillis();
          boolean should = true;
          
          RequestContext ctx = RequestContext.getCurrentContext();
          if (ctx == null || ctx.getRequest() == null) {
               should = false;
          }
          
          if (ctx != null && !ctx.sendZuulResponse()) {
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
               detail.setStackTrace(e.getClass().getName(), e.getMessage());
          } finally {
               long endTime = System.currentTimeMillis();

               long duration = (endTime - startTime);

               detail.setTimeInMillisRun(duration);
               TraceContextHolder.getInstance().getActualTrace().addFilter(getName(), detail);
          }
          return null;
     }     

     public abstract void execute() throws Throwable;
     public abstract String getName();

}
