package br.com.heimdall.middleware.util.helpermock.call;

import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class RequestMockTest {

  @Test
  public void testHeader() {
    final RequestMock requestMock = new RequestMock();
    assertEquals(new HashMap<String, String>(), requestMock.header().getAll());
  }

  @Test
  public void testQuery() {
    final RequestMock requestMock = new RequestMock();
    Assert.assertEquals(new HashMap<String, String>(), requestMock.query().getAll());
  }

  @Test
  public void testSetAppName() {
    final RequestMock requestMock = new RequestMock();
    requestMock.setAppName("testName");
    assertEquals("testName", requestMock.getAppName());
  }

  @Test
  public void testSetBody() {
    final RequestMock requestMock = new RequestMock();
    requestMock.setBody("testBody");
    assertEquals("testBody", requestMock.getBody());
  }

  @Test
  public void testSetUrl() {
    final RequestMock requestMock = new RequestMock();
    requestMock.setUrl("www.google.com");
    assertEquals("www.google.com", requestMock.getUrl());
  }

  @Test
  public void testPathParam() {
    final RequestMock requestMock = new RequestMock();
    assertNull(requestMock.pathParam("testPath"));
  }
}
