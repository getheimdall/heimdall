
package br.com.conductor.heimdall.core.exception;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import br.com.twsoftware.alfred.object.Objeto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum that concentrates the messages and validations of the exceptions <br/>
 * 
 * @author Filipe Germano
 * 
 */
@Slf4j
public enum ExceptionMessage {

     GLOBAL_ERROR_ZUUL(INTERNAL_SERVER_ERROR.value(), "Gateway Internal Server Error", ServerErrorException.class),

     GLOBAL_REQUEST_NOT_FOUND(NOT_FOUND.value(), "Request not found", NotFoundException.class),
     
     GLOBAL_RESOURCE_NOT_FOUND(NOT_FOUND.value(), "Resource not found", NotFoundException.class),

     GLOBAL_JSON_INVALID_FORMAT(BAD_REQUEST.value(), "Json invalid format", BadRequestException.class),

     GLOBAL_TIMEOUT(REQUEST_TIMEOUT.value(), REQUEST_TIMEOUT.getReasonPhrase(), TimeoutException.class),

     ACCESS_TOKEN_ALREADY_EXISTS(BAD_REQUEST.value(), "Token already exists", BadRequestException.class),
     
     INTERCEPTOR_LIMIT_REACHED(BAD_REQUEST.value(), "Intercept limit reached", BadRequestException.class),

     INTERCEPTOR_INVALID_CONTENT(BAD_REQUEST.value(), "Content for {} interceptor is incorrect. Use the standard: {}", BadRequestException.class),

     INTERCEPTOR_NOT_EXIST(BAD_REQUEST.value(), "Interceptor defined not exist", BadRequestException.class),

     INTERCEPTOR_TEMPLATE_NOT_EXIST(BAD_REQUEST.value(), "Template interceptor not exist", BadRequestException.class),

     INTERCEPTOR_REFERENCE_NOT_FOUND(BAD_REQUEST.value(), "Reference interceptor not found", BadRequestException.class),

     INTERCEPTOR_IGNORED_INVALID(BAD_REQUEST.value(), "Reference operations invalid: {}", BadRequestException.class),

     MIDDLEWARE_UNSUPPORTED_TYPE(BAD_REQUEST.value(), "File type differs from .jar not supported", BadRequestException.class),

     MIDDLEWARE_CONTAINS_INTERCEOPTORS(BAD_REQUEST.value(), "Middleware still contains interceptors associated", BadRequestException.class),
     
     MIDDLEWARE_INVALID_FILE(BAD_REQUEST.value(), "Invalid file", BadRequestException.class),

     ACCESS_TOKEN_NOT_DEFINED(BAD_REQUEST.value(), "Access token not defined", BadRequestException.class),
     
     APP_REPEATED(BAD_REQUEST.value(), "App repeated", BadRequestException.class),

     DEVELOPER_NOT_EXIST(BAD_REQUEST.value(), "Developer not exist", BadRequestException.class),

     RESOURCE_METHOD_NOT_ACCEPT(BAD_REQUEST.value(), "method not accepted please use: GET, POST, PUT, PATH or DELETE", BadRequestException.class),

     APP_NOT_EXIST(BAD_REQUEST.value(), "App not exist", BadRequestException.class),
     
     API_BASEPATH_EXIST(BAD_REQUEST.value(), "The basepath defined exist", BadRequestException.class),
     
     API_BASEPATH_EMPTY(BAD_REQUEST.value(), "Basepath not defined", BadRequestException.class),
     
     ONLY_ONE_OPERATION_PER_RESOURCE(BAD_REQUEST.value(), "Only one operation per resource", BadRequestException.class),

     ONLY_ONE_RESOURCE_PER_API(BAD_REQUEST.value(), "Only one resource per api", BadRequestException.class),

     ONLY_ONE_MIDDLEWARE_PER_VERSION_AND_API(BAD_REQUEST.value(), "Only one middleware per version and api", BadRequestException.class),
     
     ENVIRONMENT_INBOUND_URL_ALREADY_EXISTS(BAD_REQUEST.value(), "Inbound URL already exists", BadRequestException.class),
     
     PRIVILEGES_NOT_EXIST(BAD_REQUEST.value(), "Privileges {} defined to attach in role not exist ", BadRequestException.class),
     
     ACCESS_DENIED(UNAUTHORIZED.value(), "Access Denied", UnauthorizedException.class),
     
     ENVIRONMENT_ATTACHED_TO_API(BAD_REQUEST.value(), "Environment attached to Api", BadRequestException.class),
     
     OPERATION_ATTACHED_TO_INTERCEPTOR(BAD_REQUEST.value(), "Operation attached to Interceptor", BadRequestException.class),
     
     OPERATION_CANT_HAVE_SINGLE_WILDCARD(BAD_REQUEST.value(), "Operation can not have a single wild card (/*)", BadRequestException.class),
     
     OPERATION_CANT_HAVE_DOUBLE_WILDCARD_NOT_AT_THE_END(BAD_REQUEST.value(), "Operation can have a double wild card (/**), but only at the end", BadRequestException.class),
     
     API_BASEPATH_MALFORMED(BAD_REQUEST.value(), "Api basepath can not contain a wild card", BadRequestException.class);

     @Getter
     private Integer httpCode;

     @Getter
     @Setter
     private String message;

     private String defaultMessage;

     @Getter
     private Class<? extends HeimdallException> klass;

     ExceptionMessage(int httpCode, String message, Class<? extends HeimdallException> klass) {

          this.httpCode = httpCode;
          this.defaultMessage = message;
          this.klass = klass;
          this.message = Objeto.isBlank(this.message) ? this.defaultMessage.replace("{}", "") : this.message;
     }

     /**
      * Method responsible for triggering the exception
      * 
      * @throws BadRequestException
      * @throws UnauthorizedException
      * @throws ForbiddenException
      * @throws NotFoundException
      * @throws ServerErrorException
      */
     public void raise() {

          log.debug("Raising error: {}", this);

          this.message = Objeto.isBlank(this.message) ? this.defaultMessage.replace("{}", "") : this.message;

          if (this.badRequest()) {

               throw new BadRequestException(this);
          } else if (this.unauthorized()) {

               throw new UnauthorizedException(this);
          } else if (this.forbidden()) {

               throw new ForbiddenException(this);
          } else if (this.notFound()) {

               throw new NotFoundException(this);
          } else if (this.timeout()) {
               
               throw new TimeoutException(this);
          } else if (this.serverError()) {

               throw new ServerErrorException(this);
          }

     }

     /**
      * Method responsible for exception triggering with partial or total custom message inclusion.
      * 
      * @param dynamicText				This parameter will replace the symbols: {},
      * 								included in the message respectively. If more
      * 								than one symbol is given: {}, and only one parameter
      * 								pass, it will replace all the keys for the parameter entered.
      * 
      */
     public void raise(String... dynamicText) {

          if (dynamicText != null && dynamicText.length > 0) {

               Integer count = 0;
               String baseMessage = this.defaultMessage;
               while (baseMessage.contains("{}")) {

                    if (dynamicText.length == 1) {

                         this.message = this.defaultMessage.replace("{}", dynamicText[count]);
                         baseMessage = this.message;
                    } else {

                         this.defaultMessage = this.defaultMessage.replaceFirst("\\{\\}", dynamicText[count]);
                         this.message = this.defaultMessage;
                         baseMessage = this.message;
                         
                    }
                    count++;
               }
          }
          raise();
     }
     
     /**
      * Method responsible for validation of error codes with code 400.
      * 
      */
     private Boolean badRequest() {

          return this.httpCode == BAD_REQUEST.value();
     }

     /**
      * Method responsible for validation of error codes with code 401.
      * 
      */
     private Boolean unauthorized() {

          return this.httpCode == UNAUTHORIZED.value();
     }

     /**
      * Method responsible for validation of error codes with code 403.
      * 
      */
     private Boolean forbidden() {

          return this.httpCode == FORBIDDEN.value();
     }

     /**
      * Method responsible for validation of error codes with code 404.
      * 
      */
     private Boolean notFound() {

          return this.httpCode == NOT_FOUND.value();
     }

     /**
      * 
      * Method responsible for validation of error codes with code 408.
      * 
      */
     private Boolean timeout() {
          
          return this.httpCode == REQUEST_TIMEOUT.value();
     }

     /**
      * 
      * Method responsible for validation of error codes with code 500.
      * 
      */
     private Boolean serverError() {

          return this.httpCode == INTERNAL_SERVER_ERROR.value();
     }

}
