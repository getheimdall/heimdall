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
package br.com.conductor.heimdall.gateway.util;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import java.util.Comparator;

/**
 * Represents the algorithm to sort {@link ZuulRoute} correctly.
 * 
 * @author Marcos Filho
 * @author Marcelo Rodrigues
 *
 */
public class RouteSort implements Comparator<ZuulRoute> {

     private static final int BEFORE = -1;
     private static final int AFTER = 1;

     @Override
     public int compare(ZuulRoute r1, ZuulRoute r2) {

          String pattern1 = r1.getPath();
          String pattern2 = r2.getPath();

          if (pattern1.startsWith("/*")) return AFTER;
          if (pattern2.startsWith("/*")) return BEFORE;

          String[] split1 = pattern1.split("/");
          String[] split2 = pattern2.split("/");

          int max = Math.min(split1.length, split2.length);

          for (int i = 0; i < max; i++) {

               if (!split1[i].equals(split2[i])) {

                    if (split1[i].equals("**")) return AFTER;
                    if (split2[i].equals("**")) return BEFORE;

                    if (split1[i].equals("*")) return AFTER;
                    if (split2[i].equals("*")) return BEFORE;

                    return split1[i].compareTo(split2[i]);

               }

          }

          return pattern1.compareTo(pattern2);
     }


}
