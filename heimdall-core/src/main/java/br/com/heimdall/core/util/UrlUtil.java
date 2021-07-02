
package br.com.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class provides methods to handle {@link URL}s.
 * 
 * @author Thiago Sampaio
 *
 */
@Slf4j
public final class UrlUtil {

    private UrlUtil() { }

    /**
	  * Returns the current URL from a {@link HttpServletRequest}.
	  * 
	  * @param 	request			The {@link HttpServletRequest}
	  * @return					The current URL as a {@link String}
	  */
     public static String getCurrentUrl(HttpServletRequest request) {

          try {

               URL url = new URL(request.getRequestURL().toString());

               String query = request.getQueryString();
               if (query != null) {

                    return url.toString() + "?" + query;
               } else {

                    return url.toString();
               }

          } catch (Exception e) {
               log.error(e.getMessage(), e);
          }

          return null;
     }

     /**
      * Transforms a {@link String} to a {@link URL}.
      * 
      * @param 	target				The String to be converted
      * @return						The formed URL
      * @throws	IllegalStateException
      */
     public static URL getUrl(String target) {

          try {
               return new URL(target);
          } catch (MalformedURLException ex) {
               throw new IllegalStateException("Target URL is malformed", ex);
          }
     }
}
