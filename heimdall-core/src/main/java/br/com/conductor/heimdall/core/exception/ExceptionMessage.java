
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

import br.com.twsoftware.alfred.object.Objeto;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.*;

/**
 * Enum that concentrates the messages and validations of the exceptions <br/>
 * 
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
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

    INTERCEPTOR_NO_APP_FOUND(BAD_REQUEST.value(), "No App registered to this Api with client_id provided.", BadRequestException.class),

    INTERCEPTOR_NOT_EXIST(BAD_REQUEST.value(), "Interceptor defined not exist", BadRequestException.class),

    INTERCEPTOR_TEMPLATE_NOT_EXIST(BAD_REQUEST.value(), "Template interceptor not exist", BadRequestException.class),

    INTERCEPTOR_REFERENCE_NOT_FOUND(BAD_REQUEST.value(), "Reference interceptor not found", BadRequestException.class),

    INTERCEPTOR_IGNORED_INVALID(BAD_REQUEST.value(), "Reference operations invalid: {}", BadRequestException.class),

    INTERCEPTOR_INVALID_LIFECYCLE(BAD_REQUEST.value(), "{} interceptor can not be attached to the Plan life cycle", BadRequestException.class),

    MIDDLEWARE_UNSUPPORTED_TYPE(BAD_REQUEST.value(), "File type differs from .jar not supported", BadRequestException.class),

    MIDDLEWARE_CONTAINS_INTERCEPTORS(BAD_REQUEST.value(), "Middleware still contains interceptors associated", BadRequestException.class),

    MIDDLEWARE_INVALID_FILE(BAD_REQUEST.value(), "Invalid file", BadRequestException.class),

    MIDDLEWARE_PAYLOAD_TOO_LARGE(PAYLOAD_TOO_LARGE.value(), "Content is to big. Maximal allowed request size is 25MB", MultipartException.class),

    ACCESS_TOKEN_NOT_DEFINED(BAD_REQUEST.value(), "Access token not defined", BadRequestException.class),

    APP_REPEATED(BAD_REQUEST.value(), "App repeated", BadRequestException.class),

    DEVELOPER_NOT_EXIST(BAD_REQUEST.value(), "Developer not exist", BadRequestException.class),

    RESOURCE_METHOD_NOT_ACCEPT(BAD_REQUEST.value(), "method not accepted please use: GET, POST, PUT, PATH or DELETE", BadRequestException.class),

    APP_NOT_EXIST(BAD_REQUEST.value(), "App does not exist", BadRequestException.class),

    API_NOT_EXIST(BAD_REQUEST.value(), "Api does not exist", BadRequestException.class),

    API_BASEPATH_EXIST(BAD_REQUEST.value(), "The basepath defined exist", BadRequestException.class),

    API_BASEPATH_EMPTY(BAD_REQUEST.value(), "Basepath not defined", BadRequestException.class),

    API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS(BAD_REQUEST.value(), "Apis can't have environments with the same inbound url", BadRequestException.class),

    ONLY_ONE_OPERATION_PER_RESOURCE(BAD_REQUEST.value(), "Only one operation per resource", BadRequestException.class),

    ONLY_ONE_RESOURCE_PER_API(BAD_REQUEST.value(), "Only one resource per api", BadRequestException.class),
  
    SOME_PLAN_NOT_PRESENT_IN_APP(BAD_REQUEST.value(), "Some of the informed plans do not belong to the App plans", BadRequestException.class),
  
    ONLY_ONE_MIDDLEWARE_PER_VERSION_AND_API(BAD_REQUEST.value(), "Only one middleware per version and api", BadRequestException.class),
     
    ENVIRONMENT_ALREADY_EXISTS(BAD_REQUEST.value(), "Environment already exists", BadRequestException.class),

    PRIVILEGES_NOT_EXIST(BAD_REQUEST.value(), "Privileges {} defined to attach in role not exist ", BadRequestException.class),

    ACCESS_DENIED(UNAUTHORIZED.value(), "Access Denied", UnauthorizedException.class),

    ENVIRONMENT_ATTACHED_TO_API(BAD_REQUEST.value(), "Environment attached to Api", BadRequestException.class),

    ENVIRONMENT_INBOUND_DNS_PATTERN(BAD_REQUEST.value(), "Environment inbound URL has to follow the pattern http[s]://host.domain[:port] or www.host.domain[:port]", BadRequestException.class),PROVIDER_NOT_FOUND(BAD_REQUEST.value(), "Provider not found", BadRequestException.class),
     
    PROVIDER_USER_UNAUTHORIZED(UNAUTHORIZED.value(), "User provided unauthorized", UnauthorizedException.class),
     
    TOKEN_EXPIRED(UNAUTHORIZED.value(), "Token expired", UnauthorizedException.class),

    TOKEN_INVALID(UNAUTHORIZED.value(), "Token not valid", ForbiddenException.class),

    SIGNATURE_DOES_NOT_MATCH(UNAUTHORIZED.value(), "JWT signature does not match locally computed signature.", UnauthorizedException.class),

    TOKEN_NOT_GENERATE(INTERNAL_SERVER_ERROR.value(), "Error to generate token", ForbiddenException.class),

    CODE_NOT_FOUND(UNAUTHORIZED.value(), "Code already used to generate token or not defined", UnauthorizedException.class),

    GRANT_TYPE_NOT_FOUND(BAD_REQUEST.value(), "grant_type not found", BadRequestException.class),

    WRONG_GRANT_TYPE_INFORMED(BAD_REQUEST.value(), "Wrong grant_type informed", BadRequestException.class),

    REFRESH_TOKEN_NOT_FOUND(BAD_REQUEST.value(), "refresh_token not found", BadRequestException.class),

    TYPE_OAUTH_NOT_FOUND(BAD_REQUEST.value(), "TypeOAuth not found", BadRequestException.class),

    PRIVATE_KEY_NOT_FOUND(BAD_REQUEST.value(), "privateKey not found", BadRequestException.class),

    OPERATION_ATTACHED_TO_INTERCEPTOR(BAD_REQUEST.value(), "Operation attached to Interceptor", BadRequestException.class),

    OPERATION_CANT_HAVE_SINGLE_WILDCARD(BAD_REQUEST.value(), "Operation can not have a single wild card (/*)", BadRequestException.class),

    OPERATION_CANT_HAVE_DOUBLE_WILDCARD_NOT_AT_THE_END(BAD_REQUEST.value(), "Operation can have a double wild card (/**), but only at the end", BadRequestException.class),

    API_BASEPATH_MALFORMED(BAD_REQUEST.value(), "Api basepath can not contain a wild card", BadRequestException.class),

    USERNAME_OR_PASSWORD_INCORRECT(BAD_REQUEST.value(), "Username or password incorrect", BadRequestException.class),

    USERNAME_ALREADY_EXIST(BAD_REQUEST.value(), "Username already exist!", BadRequestException.class),

    EMAIL_ALREADY_EXIST(BAD_REQUEST.value(), "Email already exist!", BadRequestException.class),

    CLIENT_ID_ALREADY(BAD_REQUEST.value(), "clientId already used", BadRequestException.class),

    CLIENT_ID_NOT_FOUND(BAD_REQUEST.value(), "client_id not found", BadRequestException.class),

    AUTHORIZATION_NOT_FOUND(UNAUTHORIZED.value(), "Authorization not found in header", UnauthorizedException.class),

    RESPONSE_TYPE_NOT_FOUND(BAD_REQUEST.value(), "response_type not found", BadRequestException.class),

    DEFAULT_PROVIDER_CAN_NOT_UPDATED_OR_REMOVED(FORBIDDEN.value(), "Default Provider can't to be updated or removed!", ForbiddenException.class),

    ROLE_ALREADY_EXIST(BAD_REQUEST.value(), "Role already exist!", BadRequestException.class),

    CIRCUIT_BREAK_ACTIVE(SERVICE_UNAVAILABLE.value(), "Circuit break enabled", ServerErrorException.class),

    SCOPE_INVALID_OPERATION(BAD_REQUEST.value(), "Operation with id '{}' does not exist", BadRequestException.class),

    SCOPE_INVALID_PLAN(BAD_REQUEST.value(), "Plan id with '{}' does not exist", BadRequestException.class),

    SCOPE_OPERATION_NOT_IN_API(BAD_REQUEST.value(), "Operation '{}' not in Api '{}'", BadRequestException.class),

    SCOPE_PLAN_NOT_IN_API(BAD_REQUEST.value(), "Plan '{}' not in Api '{}'", BadRequestException.class),

    SCOPE_INVALID_NAME(BAD_REQUEST.value(), "A Scope with the provided name already exists", BadRequestException.class),

    SCOPE_NO_OPERATION_FOUND(BAD_REQUEST.value(), "A Scope must have at least one Operation", BadRequestException.class),
  
    CORS_INTERCEPTOR_NOT_API_LIFE_CYCLE(BAD_REQUEST.value(), "The CORS Interceptor only allowed for API LifeCycle", BadRequestException.class),

    CORS_INTERCEPTOR_ALREADY_ASSIGNED_TO_THIS_API(BAD_REQUEST.value(), "A CORS Interceptor already assigned to this API", BadRequestException.class),

    DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API(BAD_REQUEST.value(), "Default plan already exist to this Api", BadRequestException.class);

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
     * @param dynamicText This parameter will replace the symbols: {},
     *                    included in the message respectively. If more
     *                    than one symbol is given: {}, and only one parameter
     *                    pass, it will replace all the keys for the parameter entered.
     */
    public void raise(String... dynamicText) {

        String messageDefault = this.defaultMessage;

        if (dynamicText != null && dynamicText.length > 0) {

            int count = 0;
            String baseMessage = messageDefault;
            while (baseMessage.contains("{}")) {

                if (dynamicText.length == 1) {

                    this.message = messageDefault.replace("{}", dynamicText[count]);
                    baseMessage = this.message;
                } else {

                    messageDefault = messageDefault.replaceFirst("\\{\\}", dynamicText[count]);
                    this.message = messageDefault;
                    baseMessage = this.message;

                }
                count++;
            }
        }
        raise();
    }


    /**
     * Method responsible for validation of error codes with code 400.
     */
    private Boolean badRequest() {

        return this.httpCode == BAD_REQUEST.value();
    }

    /**
     * Method responsible for validation of error codes with code 401.
     */
    private Boolean unauthorized() {

        return this.httpCode == UNAUTHORIZED.value();
    }

    /**
     * Method responsible for validation of error codes with code 403.
     */
    private Boolean forbidden() {

        return this.httpCode == FORBIDDEN.value();
    }

    /**
     * Method responsible for validation of error codes with code 404.
     */
    private Boolean notFound() {

        return this.httpCode == NOT_FOUND.value();
    }

    /**
     * Method responsible for validation of error codes with code 408.
     */
    private Boolean timeout() {

        return this.httpCode == REQUEST_TIMEOUT.value();
    }

    /**
     * Method responsible for validation of error codes with code 500.
     */
    private Boolean serverError() {

        return this.httpCode == INTERNAL_SERVER_ERROR.value();
    }

}
