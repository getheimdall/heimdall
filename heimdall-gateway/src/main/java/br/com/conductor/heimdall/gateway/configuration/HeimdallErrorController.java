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
package br.com.conductor.heimdall.gateway.configuration;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;


import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;

/**
 * Controller class for the custom Heimdall errors.
 *
 * @author Marcos Filho
 *
 */
@Controller
public class HeimdallErrorController implements ErrorController {
     
     @Value("${error.path:/error}")
     private String errorPath;

     private static final String MESSAGE = "message";

     @Override
     public String getErrorPath() {
         return errorPath;
     }
  
     /**
      * Creates a custom error.
      * 
      * @param request		The {@link HttpServletRequest}
      * @return				{@link ResponseEntity}
      */
     @GetMapping(value = "${error.path:/error}", produces = MediaType.APPLICATION_JSON_VALUE)
     public @ResponseBody ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
          Map<String, Object> errorAttributes = new LinkedHashMap<>();
          errorAttributes.put("timestamp", LocalDateTime.now());
          final int status = getErrorStatus(request);
          errorAttributes.put("status", status);
          
          try {
               errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
          } catch (Exception ex) {
               // Unable to obtain a reason
               errorAttributes.put("error", "Http Status " + status);
          }
          
          Throwable error = getError(request);
          
          if (error != null) {
               HeimdallException exception = new HeimdallException(ExceptionMessage.GLOBAL_ERROR_ZUUL);
               
               errorAttributes.put("exception", exception.getClass().getSimpleName());
               errorAttributes.put(MESSAGE, exception.getMessage());
          }
          
          Object message = request.getAttribute("javax.servlet.error.message");
          if ((!StringUtils.isEmpty(message) || errorAttributes.get(MESSAGE) == null) && !(error instanceof BindingResult)) {
               errorAttributes.put(MESSAGE, StringUtils.isEmpty(message) ? "No message available" : message);
          }
          
          String path = (String) request.getAttribute("javax.servlet.error.request_uri");
          if (path != null) {
               errorAttributes.put("path", path);
          }
          
          return ResponseEntity.status(status).body(errorAttributes);
     }
     
     /*
      * 
      */
     private int getErrorStatus(HttpServletRequest request) {
         Integer statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
         return statusCode != null ? statusCode : HttpStatus.INTERNAL_SERVER_ERROR.value();
     }
     
     /*
      * 
      */
     private Throwable getError(HttpServletRequest request) {

          return (Throwable) request.getAttribute("javax.servlet.error.exception");
     }
     
}
