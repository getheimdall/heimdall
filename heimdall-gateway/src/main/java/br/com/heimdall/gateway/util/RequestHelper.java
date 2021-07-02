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
package br.com.heimdall.gateway.util;

import br.com.heimdall.core.environment.Property;
import br.com.heimdall.core.util.DigestUtils;
import br.com.heimdall.core.util.UrlUtil;
import br.com.heimdall.core.trace.RequestResponseParser;
import br.com.heimdall.middleware.spec.Request;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Provides methods to dump {@link Request} and get the info from the headers of a request.
 *
 * @author Marcos Filho
 *
 */
@Component
@Slf4j
public class RequestHelper {

     @Autowired
     private Property props;
     
     /**
      * Tries to create a {@link RequestResponseParser} from the current context.
      * If it fail, returns a new {@link RequestResponseParser}.
      * 
      * @return {@link RequestResponseParser}
      */
     public RequestResponseParser dumpRequest() {
          RequestContext ctx = RequestContext.getCurrentContext();
          RequestResponseParser reqDTO = new RequestResponseParser();
          HttpServletRequest request = ctx.getRequest();
          
          try {
               reqDTO.setHeaders(getRequestHeadersInfo(request));
               reqDTO.setBody(StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8));
               reqDTO.setUri(UrlUtil.getCurrentUrl(request));
		} catch (IOException e) {
		     log.error(e.getMessage(), e);
			return new RequestResponseParser(); 
		}
          
          return reqDTO;
     }
     
     /**
      * Gets the information from the headers of a {@link HttpServletRequest}.
      * 
      * @param request {@link HttpServletRequest}
      * @return			A HashMap with the information of the headers
      */
     public Map<String, String> getRequestHeadersInfo(HttpServletRequest request) {
          
          Map<String, String> map = new HashMap<>();
          Enumeration<String> headerNames = request.getHeaderNames();
          while (headerNames.hasMoreElements()) {
               String key = headerNames.nextElement();
               String value;

               if (props.getTrace().getSanitizes() != null) {
                    if (props.getTrace().getSanitizes().contains(key)) {
                         value = DigestUtils.digestMD5(request.getHeader(key));
                    } else {
                         value = request.getHeader(key);     
                    }
               } else {
                    value = request.getHeader(key);
               }
               
               map.put(key, value);
          }
          
          return map;
     }
}
