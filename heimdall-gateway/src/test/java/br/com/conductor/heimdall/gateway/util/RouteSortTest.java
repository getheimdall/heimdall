package br.com.conductor.heimdall.gateway.util;

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

	
     List<ZuulRoute> expected = new LinkedList<>();
     List<ZuulRoute> actual = new LinkedList<>();
     
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
