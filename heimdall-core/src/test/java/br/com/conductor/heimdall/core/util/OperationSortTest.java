
package br.com.conductor.heimdall.core.util;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.*;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.After;
import org.junit.Test;

import br.com.conductor.heimdall.core.entity.Operation;

public class OperationSortTest {
     List<Operation> expected = new LinkedList<>();
     List<Operation> actual = new LinkedList<>();

     @After
     public void afterTestMethod() {
         expected.clear();
         actual.clear();
     }
     
     @Test
     public void routeWithWildCardNeedBeTheLast() {
          
          Operation r1 = new Operation();
          Operation r2 = new Operation();
          Operation r3 = new Operation();
          Operation r4 = new Operation();
          Operation r5 = new Operation();
          
          r1.setPath("/cartoes");
          r2.setPath("/cartoes/*");
          r3.setPath("/foo");
          r4.setPath("/foo/*");
          r5.setPath("/*");
          
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

          actual.sort(new OperationSort());

          assertThat(actual, IsCollectionWithSize.hasSize(5));
          assertTrue(actual.get(actual.size() - 1).getPath().startsWith("/*"));
          assertThat(actual, is(expected));
     }

     @Test
     public void routeWithSamePrefix() {

          Operation r1 = new Operation();
          Operation r2 = new Operation();
          
          r1.setPath("/foo/bar");
          r2.setPath("/foo/bar/alpha/beta");

          actual.add(r2);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);

          actual.sort(new OperationSort());

          assertThat(actual, is(expected));
     }

     @Test
     public void routeWithSamePrefixAndSingleWildCard() {
        
          Operation r1 = new Operation();
          Operation r2 = new Operation();
          
          r1.setPath("/foo/bar");
          r2.setPath("/foo/bar/*");

          actual.add(r2);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);

          actual.sort(new OperationSort());

          assertThat(actual, is(expected));
     }
     
     @Test
     public void routeWithSamePrefixAndDoubleWildCard() {

          Operation r1 = new Operation();
          Operation r2 = new Operation();
          
          r1.setPath("/foo/bar");
          r2.setPath("/foo/bar/**");

          actual.add(r2);
          actual.add(r1);

          expected.add(r1);
          expected.add(r2);

          actual.sort(new OperationSort());

          assertThat(actual, is(expected));
     }

     @Test
     public void routeWithSamePrefixAndDoubleWildCardAndSingleWildCard() {
          
          Operation r1 = new Operation();
          Operation r2 = new Operation();
          Operation r3 = new Operation();

          r1.setPath("/foo/bar");
          r2.setPath("/foo/bar/*");
          r3.setPath("/foo/bar/**");

          actual.add(r3);
          actual.add(r2);
          actual.add(r1);
          
          expected.add(r1);
          expected.add(r2);
          expected.add(r3);

          actual.sort(new OperationSort());

          assertThat(actual, is(expected));
     }
     
     @Test
     public void routeWithSamePrefixValidPathAndWildCard() {
          
          Operation r1 = new Operation();
          Operation r2 = new Operation();
          Operation r3 = new Operation();
          Operation r4 = new Operation();
          
          r1.setPath("/foo/bar");
          r2.setPath("/foo/bar/alpha");
          r3.setPath("/foo/bar/*");
          r4.setPath("/*");
          
          actual.add(r4);
          actual.add(r3);
          actual.add(r2);
          actual.add(r1);
          
          expected.add(r1);
          expected.add(r2);
          expected.add(r3);
          expected.add(r4);

          actual.sort(new OperationSort());

          assertThat(actual, is(expected));
     }

}
