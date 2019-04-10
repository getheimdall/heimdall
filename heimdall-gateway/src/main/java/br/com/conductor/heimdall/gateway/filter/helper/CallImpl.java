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
package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.gateway.trace.StackTraceImpl;
import br.com.conductor.heimdall.gateway.trace.TraceContextHolder;
import br.com.conductor.heimdall.gateway.util.ConstantsContext;
import br.com.conductor.heimdall.middleware.spec.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.zuul.context.RequestContext;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Implementation of the {@link Call} interface.
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Slf4j
public class CallImpl implements Call {

     private RequestContext context;
     private ThreadLocal<byte[]> buffers;
     
     public CallImpl(ThreadLocal<byte[]> buffers) {

          this.buffers = buffers;
          context = RequestContext.getCurrentContext();
     }
     
     @Override
     public Request request() {
          
          Request request = new RequestImpl();
          return request;
     }
     
     public class RequestImpl implements Request {

          @Override
          public Header header() {
               
               Header header = new HeaderImpl();
               return header;
          }
                    
          public class HeaderImpl implements Header {

               @Override
               public Map<String, String> getAll() {

                    HttpServletRequest r = context.getRequest();
                    List<String> names = Collections.list(r.getHeaderNames());
                    
                    Map<String, String> headers = new HashMap<>();
                    names.forEach(name -> {
                         
                         if (r.getHeader(name) != null) {
                              
                              headers.put(name, r.getHeader(name));
                         }
                    });
                    
                    return headers;     
               }

               @Override
               public String get(String name) {

                    HttpServletRequest r = context.getRequest();
                    
                    String value = r.getHeader(name);
                    if (value != null) {
                         
                         value = context.getZuulRequestHeaders().get(name);
                    }
                    
                    return value;
               }

               @Override
               public void set(String name, String value) {
                    
                    if (name != null && value != null) {

                         context.addZuulRequestHeader(name, value);
                    }
               }

               @Override
               public void add(String name, String value) {

                    this.set(name, value);
               }

               @Override
               public void addAll(Map<String, String> values) {

                    values.forEach((k, v) -> context.addZuulRequestHeader(k, v));
               }

               @Override
               public void remove(String name) {

                    if (name != null) {

                         HttpServletRequestWrapper requestWrapper = removeRequestHeaderWrapper(context.getRequest(), name);
                         context.setRequest(requestWrapper);
                    }
               }

               @Override
               public String getMethod() {
                    
                    return context.getRequest().getMethod();
               }

          }

          @Override
          public Query query() {
               
               Query query = new QueryImpl();
               return query;
          }

          public class QueryImpl implements Query {

               @Override
               public Map<String, String> getAll() {

                    HttpServletRequest r = context.getRequest();
                    List<String> names = Collections.list(r.getParameterNames());
                    
                    Map<String, String> params = new HashMap<>();
                    names.forEach(name -> {
                         if (r.getParameter(name) != null) {
                              params.put(name, r.getParameter(name));
                         }
                    });
                    
                    return params; 
               }

               @Override
               public String get(String name) {

                    HttpServletRequest r = context.getRequest();
                    
                    return r.getParameter(name);
               }

               @Override
               public void set(String name, String value) {

                    add(name, value);
               }

               @Override
               public void add(String name, String value) {

                    if (value != null) {

                         RequestContext context = RequestContext.getCurrentContext();

                         Map<String, List<String>> params = context.getRequestQueryParams();

                         if (params == null) {

                              params = new ConcurrentHashMap<>();
                         }
                         params.put(name, Arrays.asList(value));
                         context.setRequestQueryParams(params);
                    }
               }

               @Override
               public void remove(String name) {

                    RequestContext context = RequestContext.getCurrentContext();

                    Map<String, List<String>> params = context.getRequestQueryParams();

                    if (params != null) {

                         params.remove(name);
                    }

                    context.setRequestQueryParams(params);
               }

          }

          @Override
          public String getBody() {
               
               try (InputStream in = (InputStream) context.get("requestEntity")) {
                    String bodyText;
            	    if (in == null) {
                         bodyText = StreamUtils.copyToString(context.getRequest().getInputStream(), Charset.forName("UTF-8"));
                    } else {
                         bodyText = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
                    }

                    return bodyText;
               } catch (Exception e) {

                    log.error(e.getMessage(), e);
                    return null;
               }

          }
          
          @Override
          public void setBody(String body) {
               
               try {
                    
                    context.set("requestEntity", new ByteArrayInputStream(body.getBytes("UTF-8")));
               } catch (Exception e) {
                    
                    log.error(e.getMessage(), e);
               }
          }
          
          @Override
          public void setUrl(String routeUrl) {
          
               String url = null;
               URL urlParse = null;
               try {
                    
                    url = UriComponentsBuilder.fromHttpUrl(routeUrl).build().toUriString();
                    urlParse = new URL(url);
               } catch (Exception e) {
                    
                    log.error(e.getMessage(), e);
               }
               
               if (url != null) {
                    
                    context.setRouteHost(urlParse);
                    context.set("requestURI", "");
               }
          }

          @Override
          public String getUrl() {
               return context.getRequest().getRequestURI();
          }
          
          @Override
          public String pathParam(String name) {
               
               if (name != null) {
                    Object requestURI = context.get("requestURI");
                    Object pattern = context.get("pattern");

                    name = "{"+ name +"}";
                    
                    String patternText = pattern != null ? pattern.toString() : "";
                    String requestURIText = requestURI != null ? requestURI.toString() : "";
                    String separator = "/";
                    String[] a = patternText.split(separator);
                    String[] b = requestURIText.split(separator);

                    String value = null;

                    for (int i = 0; i < a.length; i++) {

                         if (a[i].equals(name) && !a[i].equals(b[i])) {

                              value = b[i];
                              break;
                         }
                    }

                    return value;
               } else {
                    
                    return null;
               }
               
          }
          
          @Override
          public String getAppName() {
               
               return TraceContextHolder.getInstance().getActualTrace().getApp();
          }

          @Override
          public void setSendResponse(boolean value) {

               context.setSendZuulResponse(value);
          }
          
     }

     @Override
     public Response response() {
          
          Response response = new ResponseImpl();
          return response;
     }
     
     public class ResponseImpl implements Response {
          
          @Override
          public Header header() {
               
               Header header = new HeaderImpl();
               return header;
          }
          
          public class HeaderImpl implements Header {
               
               @Override
               public Map<String, String> getAll() {

                    HttpServletResponse r = context.getResponse();
                    List<String> names = new ArrayList<>(r.getHeaderNames());
                    
                    Map<String, String> headers = new HashMap<>();
                    names.forEach(name -> {
                         
                         if (r.getHeader(name) != null) {
                              
                              headers.put(name, r.getHeader(name));
                         }
                    });
                    
                    return headers; 
               }
               
               @Override
               public String get(String name) {
                    
                    HttpServletResponse r = context.getResponse();
                    
                    return r.getHeader(name);
               }

               @Override
               public void set(String name, String value) {
                    
                    HttpServletResponse r = context.getResponse();
                    
                    r.setHeader(name, value);
               }
               
               @Override
               public void add(String name, String value) {
                    
                    HttpServletResponse r = context.getResponse();
                    
                    r.addHeader(name, value);
               }

               @Override
               public void addAll(Map<String, String> values) {
                    HttpServletResponse r = context.getResponse();

                    values.forEach(r::addHeader);
               }

               @Override
               public void remove(String name) {
                    
                    if (name != null) {

                         HttpServletResponseWrapper responseWrapper = removeResponseHeaderWrapper(context.getResponse(), name);

                         context.setResponse(responseWrapper);

                    }
               }

               @Override
               public String getMethod() {

                    return null;
               }
               
          }
          
          @Override
          public Integer getStatus() {
               
               return context.getResponse().getStatus(); 
          }

          @Override
          public void setStatus(Integer status) {
               
               context.getResponse().setStatus(status);
          }

          @Override
          public String getBody() {
               
               return context.getResponseBody();
          }
          
          @Override
          public void setBody(String body) {
               
               context.setSendZuulResponse(false);
               context.setResponseBody(body);
               
          }

          @Override
          public void setBody(byte[] body) {
              setBody(body, false);
          }

         @Override
         public void setBody(byte[] body, boolean gzip) {

             InputStream stream;
             try {
                 if (body != null) {
                     stream = new ByteArrayInputStream(body);
                     if (gzip) {
                         stream = new GZIPInputStream(stream);
                     }
                 } else {
                     stream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
                 }
                 context.setSendZuulResponse(false);
                 context.setResponseDataStream(stream);
                 writeResponse(stream, context.getResponse().getOutputStream());

             } catch (UnsupportedEncodingException e) {
                 log.error(e.getMessage(), e);
             } catch (IOException e) {
                 e.printStackTrace();
             }

         }

         private void writeResponse(InputStream zin, OutputStream out) throws IOException {
        	 byte[] bytes = buffers.get();
             int bytesRead = -1;
             while ((bytesRead = zin.read(bytes)) != -1) {
                 out.write(bytes, 0, bytesRead);
             }
         }
     }
     
     @Override
     public Trace trace() {
          
          Trace trace = new TraceImpl();
          return trace;
     }
     
     public class TraceImpl implements Trace {
          
          public void setStackTrace(StackTrace stackTrace) {
               
               TraceContextHolder.getInstance().getActualTrace().setStackTrace(stackTrace);
          }
          
          @Override
          public void addStackTrace(String clazz, String message, String stack) {
               
               TraceContextHolder.getInstance().getActualTrace().setStackTrace(new StackTraceImpl(clazz, message, stack));
          }
          
          public StackTrace getStackTrace() {
               
               return TraceContextHolder.getInstance().getActualTrace().getStackTrace();
          }

          public void addTrace(String trace) {

               TraceContextHolder.getInstance().getActualTrace().trace(trace);
          }

          public void addTrace(String trace, Object object) {
               
               TraceContextHolder.getInstance().getActualTrace().trace(trace, object);
          }

     }
     
     @Override
     public Environment environment() {
          
          Environment environment = new EnvironmentImpl();
          return environment;
     }
     
     public class EnvironmentImpl implements Environment {
          
          private Map<String, String> currentVariables;
          
          @SuppressWarnings("unchecked")
          public EnvironmentImpl() {
        	  currentVariables = (Map<String, String>) context.get(ConstantsContext.ENVIRONMENT_VARIABLES);
        	  if (Objects.isNull(currentVariables)) {
                    currentVariables = new HashMap<>();
               }
               
          }

          @Override
          public Map<String, String> getVariables() {

               return currentVariables;
          }

          @Override
          public String getVariable(String key) {

               String value = currentVariables.get(key);
               if (StringUtils.isBlank(value)) {
                    
                    TraceContextHolder.getInstance().getActualTrace().trace("Environment variable with key '" + key + "' not exist.");
               }
               
               return value;
          }

     }
     
     @Override
     public Info info() {
          
          Info info = new InfoImpl();
          return info;
     }
     
     public class InfoImpl implements Info {
          
          public String appName() {
               
               return TraceContextHolder.getInstance().getActualTrace().getApp();
          }

          public String apiName() {
               
               return TraceContextHolder.getInstance().getActualTrace().getApiName();
          }

          public Long apiId() {
               
               return TraceContextHolder.getInstance().getActualTrace().getApiId();
          }
          
          public String developer() {
               
               return TraceContextHolder.getInstance().getActualTrace().getAppDeveloper();
          }
          
          public String method() {
               
               return TraceContextHolder.getInstance().getActualTrace().getMethod();
          }

          public String clientId() {
               
               return TraceContextHolder.getInstance().getActualTrace().getClientId();
          }

          public String accessToken() {
               
               return TraceContextHolder.getInstance().getActualTrace().getAccessToken();
          }

          public String pattern() {
               
               return TraceContextHolder.getInstance().getActualTrace().getPattern();
          }

          public Long operationId() {
               
               return TraceContextHolder.getInstance().getActualTrace().getOperationId();
          }

          public String profile() {
               
               return TraceContextHolder.getInstance().getActualTrace().getProfile();
          }
          
          public Long resourceId() {
               
               return TraceContextHolder.getInstance().getActualTrace().getResourceId();
          }

          public String url() {
               
               return TraceContextHolder.getInstance().getActualTrace().getUrl();
          }

          public String requestURI() {
               
               return context.getRequest().getRequestURI();
          }
          
     }
     
     //
     // Private helper methods
     //
     
     private HttpServletRequestWrapper removeRequestHeaderWrapper(HttpServletRequest request, String name) {

          return new HttpServletRequestWrapper(request) {

               public String getHeader(String nameHeader) {

                    String valueHeader = null;
                    if (name != null && !name.equalsIgnoreCase(nameHeader)) {

                         valueHeader = super.getHeader(nameHeader);
                    }

                    return valueHeader;
               }

               public Enumeration<String> getHeaderNames() {

                    List<String> names = Collections.list(super.getHeaderNames());
                    
                    if (name != null && names.stream().anyMatch(s -> s.equalsIgnoreCase(name))) {
                         
                         names.remove(name);
                    }

                    return Collections.enumeration(names);
               }
          };
     }
     
     private HttpServletResponseWrapper removeResponseHeaderWrapper(HttpServletResponse response, String name) {

          return new HttpServletResponseWrapper(response) {

               public void addHeader(String headerName, String headerValue) {

                    if (!name.equalsIgnoreCase(headerName)) {

                         super.addHeader(headerName, headerValue);
                    }

               }
          };
     }
     
}
