
package br.com.conductor.heimdall.middleware.exception;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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

import org.springframework.http.HttpStatus;

import com.github.thiagonego.alfred.object.Objeto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Enum that concentrates the messages and validations of the exceptions.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
public enum ExceptionMessage {

     INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), ServerErrorException.class),
     
     BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), BadRequestException.class),
     
     FORBIDDEN(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), ForbiddenException.class),
     
     NOT_FOUND(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), NotFoundException.class),
     
     UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), UnauthorizedException.class),

     INTERNAL_SERVER_ERROR_CUSTOM(HttpStatus.INTERNAL_SERVER_ERROR.value(), "{}", ServerErrorException.class),
     
     BAD_REQUEST_CUSTOM(HttpStatus.BAD_REQUEST.value(), "{}", BadRequestException.class),
     
     FORBIDDEN_CUSTOM(HttpStatus.FORBIDDEN.value(), "{}", ForbiddenException.class),
     
     NOT_FOUND_CUSTOM(HttpStatus.NOT_FOUND.value(), "{}", NotFoundException.class),
     
     UNAUTHORIZED_CUSTOM(HttpStatus.UNAUTHORIZED.value(), "{}", UnauthorizedException.class),

     ;
     
     
     @Getter
     private Integer httpCode;

     @Getter
     @Setter
     private String message;

     private String defaultMessage;

     @Getter
     private Class<? extends MiddlewareException> klass;

     ExceptionMessage(int httpCode, String message, Class<? extends MiddlewareException> klass) {

          this.httpCode = httpCode;
          this.defaultMessage = message;
          this.klass = klass;
          this.message = Objeto.isBlank(this.message) ? this.defaultMessage.replace("{}", "") : this.message;
     }

     /**
      * 
      * Method responsible for triggering the exception
      * 
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
          } else if (this.serverError()) {

               throw new ServerErrorException(this);
          }

     }

     /**
      * 
      * Method responsible for exception triggering with partial or total custom message inclusion.
      * 
      * @param dynamicText
      * This parameter will replace the symbols: {}, included in the message respectively. If more than one symbol is given: {}, and only one parameter pass, it will replace all the keys for the parameter entered.
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
      * 
      * Method responsible for validation of error codes with code 400.
      * 
      */
     private Boolean badRequest() {

          return this.httpCode == HttpStatus.BAD_REQUEST.value();
     }

     /**
      * 
      * Method responsible for validation of error codes with code 401.
      * 
      */
     private Boolean unauthorized() {

          return this.httpCode == HttpStatus.UNAUTHORIZED.value();
     }

     /**
      * 
      * Method responsible for validation of error codes with code 403.
      * 
      * 
      */
     private Boolean forbidden() {

          return this.httpCode == HttpStatus.FORBIDDEN.value();
     }

     /**
      * 
      * Method responsible for validation of error codes with code 404.
      * 
      */
     private Boolean notFound() {

          return this.httpCode == HttpStatus.NOT_FOUND.value();
     }

     /**
      * 
      * Method responsible for validation of error codes with code 500.
      * 
      */
     private Boolean serverError() {

          return this.httpCode == HttpStatus.INTERNAL_SERVER_ERROR.value();
     }

}
