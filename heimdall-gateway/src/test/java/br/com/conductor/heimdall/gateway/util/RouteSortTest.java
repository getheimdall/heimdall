package br.com.conductor.heimdall.gateway.util;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Test;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

public class RouteSortTest {

	
	@Test
	public void routeWithWildCardNeedBeTheLast() {
		
		List<ZuulRoute> routes = new LinkedList<>();
		ZuulRoute r2 = new ZuulRoute("/cartoes", "");
		ZuulRoute r1 = new ZuulRoute("/*", "");
		ZuulRoute r3 = new ZuulRoute("/foo/*", "");
		ZuulRoute r4 = new ZuulRoute("/foo", "");
		ZuulRoute r5 = new ZuulRoute("/cartoes/*", "");
		
		routes.add(r1);
		routes.add(r2);
		routes.add(r3);
		routes.add(r4);
		routes.add(r5);
		
		routes.sort(new RouteSort());
		
		assertThat(routes, IsCollectionWithSize.hasSize(5));
		assertTrue(routes.get(routes.size() - 1).getPath().startsWith("/*"));
	}
}
