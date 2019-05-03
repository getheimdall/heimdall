package br.com.conductor.heimdall.gateway.service;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.netflix.zuul.context.RequestContext;

@RunWith(MockitoJUnitRunner.class)
public class IpsInterceptorServiceTest {

	@InjectMocks
    private IpsInterceptorService ipsInterceptorService;
	
	@Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        RequestContext ctx = RequestContext.getCurrentContext();

        ctx.setResponse(new MockHttpServletResponse());
    }
	
	@Test
    public void blockIpsOutOfRangeInWhitelist() throws Throwable {
		RequestContext ctx = RequestContext.getCurrentContext();
		MockHttpServletRequest mockHttp = new MockHttpServletRequest();
        mockHttp.addHeader("X-FORWARDED-FOR", "10.60.40.50");
        mockHttp.setRemoteAddr("192.168.12.128");
        
        ctx.setRequest(mockHttp);
		
		Set<String> whitelist = new HashSet<>();
		whitelist.add("192.168.12.0/25");
		
		ipsInterceptorService.executeWhiteList(whitelist);
		
		Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), ctx.getResponse().getStatus());
	}
	
	@Test
    public void allowIpsWithRangeInWhitelist() throws Throwable {
		RequestContext ctx = RequestContext.getCurrentContext();
		MockHttpServletRequest mockHttp = new MockHttpServletRequest();
        mockHttp.addHeader("X-FORWARDED-FOR", "10.60.40.50");
        mockHttp.setRemoteAddr("192.168.12.126");
        
        ctx.setRequest(mockHttp);
		
		Set<String> whitelist = new HashSet<>();
		whitelist.add("192.168.12.0/25");
		
		ipsInterceptorService.executeWhiteList(whitelist);
		
		Assert.assertEquals(HttpStatus.OK.value(), ctx.getResponse().getStatus());
	}
	
	@Test
    public void allowIpsWithPortInWhitelist() throws Throwable {
		RequestContext ctx = RequestContext.getCurrentContext();
		MockHttpServletRequest mockHttp = new MockHttpServletRequest();
        mockHttp.addHeader("X-FORWARDED-FOR", "192.168.12.128:3000");
        mockHttp.setRemoteAddr("10.60.40.50");
        
        ctx.setRequest(mockHttp);
		
		Set<String> whitelist = new HashSet<>();
		whitelist.add("192.168.12.128");
		
		ipsInterceptorService.executeWhiteList(whitelist);
		
		Assert.assertEquals(HttpStatus.OK.value(), ctx.getResponse().getStatus());
	}
	
	@Test
    public void blockIpsInBlacklist() throws Throwable {
		RequestContext ctx = RequestContext.getCurrentContext();
		MockHttpServletRequest mockHttp = new MockHttpServletRequest();
        mockHttp.addHeader("X-FORWARDED-FOR", "192.168.12.128:3000");
        mockHttp.setRemoteAddr("10.60.40.50");
        
        ctx.setRequest(mockHttp);
		
		Set<String> blacklist = new HashSet<>();
		blacklist.add("192.168.12.128");
		
		ipsInterceptorService.executeBlackList(blacklist);
		
		Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), ctx.getResponse().getStatus());
	}
	
	@Test
    public void blockIpsWithRangeInBlacklist() throws Throwable {
		RequestContext ctx = RequestContext.getCurrentContext();
		MockHttpServletRequest mockHttp = new MockHttpServletRequest();
        mockHttp.addHeader("X-FORWARDED-FOR", "192.168.12.127:3000");
        mockHttp.setRemoteAddr("10.60.40.50");
        
        ctx.setRequest(mockHttp);
		
		Set<String> blacklist = new HashSet<>();
		blacklist.add("192.168.12.0/25");
		
		ipsInterceptorService.executeBlackList(blacklist);
		
		Assert.assertEquals(HttpStatus.UNAUTHORIZED.value(), ctx.getResponse().getStatus());
	}
	
	@Test
    public void allowIpsOutOfRangeInBlacklist() throws Throwable {
		RequestContext ctx = RequestContext.getCurrentContext();
		MockHttpServletRequest mockHttp = new MockHttpServletRequest();
        mockHttp.addHeader("X-FORWARDED-FOR", "192.168.12.128:3000");
        mockHttp.setRemoteAddr("10.60.40.50");
        
        ctx.setRequest(mockHttp);
		
		Set<String> blacklist = new HashSet<>();
		blacklist.add("192.168.12.0/25");
		
		ipsInterceptorService.executeBlackList(blacklist);
		
		Assert.assertEquals(HttpStatus.OK.value(), ctx.getResponse().getStatus());
	}
	
}
