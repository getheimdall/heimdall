
package br.com.conductor.heimdall.gateway.trace;

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

import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.LocalDateTimeSerializer;
import br.com.conductor.heimdall.core.util.UrlUtil;
import br.com.conductor.heimdall.middleware.spec.StackTrace;
import br.com.twsoftware.alfred.object.Objeto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;

import static net.logstash.logback.marker.Markers.append;

/**
 * Represents the trace message.
 *
 * @author Thiago Sampaio
 *
 */
@Data
@Slf4j
public class Trace {
	
	 private static final Logger logMongo = LoggerFactory.getLogger("mongo");

     private String method;

     private String url;

     private int resultStatus;

     @JsonIgnore
     private Long initialTime;

     private Long durationMillis;

     @JsonSerialize(using = LocalDateTimeSerializer.class)
     private LocalDateTime insertedOnDate = LocalDateTime.now();

     private Long apiId;

     private String apiName;

     private String app;

     private String accessToken;

     private String receivedFromAddress;

     private String clientId;

     private Long resourceId;

     private String appDeveloper;
     
     private Long operationId;
     
     private RequestResponseParser request;
     
     private RequestResponseParser response;

     private String pattern;
     
     @JsonInclude(Include.NON_NULL)
     private StackTrace stackTrace;

     @Getter
     private List<GeneralTrace> traces = Lists.newArrayList();
     
     @Getter
     private List<FilterDetail> filters = Lists.newArrayList();

     private String profile;
     
     @JsonIgnore
     private boolean printAllTrace;
     @JsonIgnore
     private boolean printMongo;

     @JsonIgnore
     private boolean shouldPrint;
     
     public Trace() {
    	 
     }

     /**
      * Creates a Trace.
      * 
      * @param printAllTrace	boolean, should print all trace
      * @param profile			String, profile
      * @param servletRequest	{@link ServletRequest}
      */
     public Trace(boolean printAllTrace, String profile, ServletRequest servletRequest, boolean printMongo){

          this.shouldPrint = true;
          this.profile = profile;
          this.printAllTrace = printAllTrace;
          this.printMongo = printMongo;
          HttpServletRequest request = (HttpServletRequest) servletRequest;
          HeimdallException.checkThrow(request == null, ExceptionMessage.GLOBAL_REQUEST_NOT_FOUND);

          setInitialTime(System.currentTimeMillis());
          setMethod(request.getMethod());
          setUrl(UrlUtil.getCurrentUrl(request));

          Enumeration<String> headers = request.getHeaders("x-forwarded-for");
          if (Objeto.notBlank(headers)) {

               List<String> listaIPs = Lists.newArrayList();
               while (headers.hasMoreElements()) {
                    String ip = headers.nextElement();
                    listaIPs.add(ip);
               }

               setReceivedFromAddress(Joiner.on(",").join(listaIPs.toArray()));

          }

     }
     
     /**
      * Adds a {@link FilterDetail} to the List.
      * 
      * @param detail {@link FilterDetail}
      */
     public void addFilter(FilterDetail detail) {
          filters.add(detail);
     }

     /**
      * Creates and adds a new trace to the traces List.
      * 
      * @param msg	Message to be added to the trace
      * @return		{@link Trace} created
      */
     public Trace trace(String msg) {

          traces.add(new GeneralTrace(msg));

          return this;

     }

     /**
      * Creates and adds a new trace from message and Object.
      * 
      * @param msg		The message for the trace
      * @param object	The Object to be added to the trace
      * @return			{@link Trace} created
      */
     public Trace trace(String msg, Object object) {

          traces.add(new GeneralTrace(msg, object));

          return this;

     }

     /**
      * Writes a {@link HttpServletResponse} to the Heimdall Trace
      * 
      * @param response	{@link HttpServletResponse}
      */
     public void write(HttpServletResponse response) {

          try {

               setResultStatus(response.getStatus());
               setDurationMillis(System.currentTimeMillis() - getInitialTime());
               
               prepareLog(response.getStatus());

          } catch (Exception e) {

               log.error(e.getMessage(), e);

          } finally {

               TraceContextHolder.getInstance().clearActual();
          }

     }

     /*
      * Heimdall uses three levels of log depending on the status code of the response.
      *
      * Levels per status code range:
      *   * 1xx~2xx = INFO
      *   * 3xx~4xx = WARN
      *   * OTHER   = ERROR
      */
     private void prepareLog(Integer statusCode) throws JsonProcessingException {

          if (printAllTrace) {

               if (isInfo(statusCode)) {

                    log.info(" [HEIMDALL-TRACE] - {} ", new ObjectMapper().writeValueAsString(this));
               } else if (isWarn(statusCode)) {

                    log.warn(" [HEIMDALL-TRACE] - {} ", new ObjectMapper().writeValueAsString(this));
               } else {

                    log.error(" [HEIMDALL-TRACE] - {} ", new ObjectMapper().writeValueAsString(this));
               }
          } else {

               String url = (Objeto.notBlank(getUrl())) ? getUrl() : "";

               if (isInfo(statusCode)) {

                    log.info(append("call", this), " [HEIMDALL-TRACE] - " + url);

                    if (printMongo) logMongo.info(new ObjectMapper().writeValueAsString(this));
               } else if (isWarn(statusCode)) {

                    log.warn(append("call", this), " [HEIMDALL-TRACE] - " + url);
                    if (printMongo) logMongo.warn(new ObjectMapper().writeValueAsString(this));
               } else {

                    log.error(append("call", this), " [HEIMDALL-TRACE] - " + url);
                    if (printMongo) logMongo.error(new ObjectMapper().writeValueAsString(this));
               }
          }
     }

     /*
      * Checks if the status code is in range 1xx to 2xx
      */
     private static boolean isInfo(Integer statusCode) {
          return HttpStatus.valueOf(statusCode).is1xxInformational() ||
                  HttpStatus.valueOf(statusCode).is2xxSuccessful();
     }

     /*
      * Checks if the status code is in range 3xx to 4xx
      */
     private static boolean isWarn(Integer statusCode) {
          return HttpStatus.valueOf(statusCode).is3xxRedirection() ||
                  HttpStatus.valueOf(statusCode).is4xxClientError();
     }

}
