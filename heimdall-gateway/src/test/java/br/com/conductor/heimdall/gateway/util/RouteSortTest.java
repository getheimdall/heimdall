
package br.com.conductor.heimdall.gateway.util;

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

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.After;
import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

public class RouteSortTest {

	
     private List<ZuulRoute> expected = new LinkedList<>();
     private List<ZuulRoute> actual = new LinkedList<>();
     
     @After
     public void afterTestMethod() {
         expected.clear();
         actual.clear();
     }

     @Test
     public void routeWithWildCardNeedBeTheLast() {

          ZuulRoute r1 = new ZuulRoute("/cartoes", "");
          ZuulRoute r2 = new ZuulRoute("/cartoes/*", "");
          ZuulRoute r3 = new ZuulRoute("/foo", "");
          ZuulRoute r4 = new ZuulRoute("/foo/*", "");
          ZuulRoute r5 = new ZuulRoute("/*", "");
          
          actual.add(r5);
          actual.add(r3);
          actual.add(r2);
          actual.add(r4);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);
          expected.add(r3);
          expected.add(r4);
          expected.add(r5);

          actual.sort(new RouteSort());

          assertThat(actual, IsCollectionWithSize.hasSize(5));
          assertTrue(actual.get(actual.size() - 1).getPath().startsWith("/*"));
          assertThat(actual, is(expected));
     }

     @Test
     public void routeWithSamePrefix() {

          ZuulRoute r1 = new ZuulRoute("/foo/bar", "");
          ZuulRoute r2 = new ZuulRoute("/foo/bar/alpha/beta", "");
          
          actual.add(r2);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);

          actual.sort(new RouteSort());

          assertThat(actual, is(expected));
     }

     @Test
     public void routeWithSamePrefixAndSingleWildCard() {
        
          ZuulRoute r1 = new ZuulRoute("/foo/bar", "");
          ZuulRoute r2 = new ZuulRoute("/foo/bar/*", "");
          
          actual.add(r2);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);

          actual.sort(new RouteSort());

          assertThat(actual, is(expected));
     }
     
     @Test
     public void routeWithSamePrefixAndDoubleWildCard() {

          ZuulRoute r1 = new ZuulRoute("/foo/bar", "");
          ZuulRoute r2 = new ZuulRoute("/foo/bar/**", "");
          
          actual.add(r2);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);

          actual.sort(new RouteSort());

          assertThat(actual, is(expected));
     }

     @Test
     public void routeWithSamePrefixAndDoubleWildCardAndSingleWildCard() {
          
          ZuulRoute r1 = new ZuulRoute("/foo/bar", "");
          ZuulRoute r2 = new ZuulRoute("/foo/bar/*", "");
          ZuulRoute r3 = new ZuulRoute("/foo/bar/**", "");
          
          actual.add(r3);
          actual.add(r2);
          actual.add(r1);
          
          expected.add(r1);
          expected.add(r2);
          expected.add(r3);

          actual.sort(new RouteSort());

          assertThat(actual, is(expected));
     }
     
     @Test
     public void routeWithSamePrefixValidPathAndWildCard() {
          
          ZuulRoute r1 = new ZuulRoute("/foo/bar", "");
          ZuulRoute r2 = new ZuulRoute("/foo/bar/alpha", "");
          ZuulRoute r3 = new ZuulRoute("/foo/bar/*", "");
          ZuulRoute r4 = new ZuulRoute("/*", "");
          
          actual.add(r4);
          actual.add(r3);
          actual.add(r2);
          actual.add(r1);
          
          expected.add(r1);
          expected.add(r2);
          expected.add(r3);
          expected.add(r4);

          actual.sort(new RouteSort());

          assertThat(actual, is(expected));
     }
}
