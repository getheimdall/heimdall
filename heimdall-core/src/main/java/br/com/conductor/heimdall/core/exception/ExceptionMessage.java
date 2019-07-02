
package br.com.conductor.heimdall.core.exception;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * Enum that concentrates the messages and validations of the exceptions <br/>
 *
 * @author Filipe Germano
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 * @author Marcelo Aguiar Rodrigues
 * @author <a href="https://github.com/felipe-brito" target="_blank" >Felipe Brito</a>
 *
 */
@Slf4j
public enum ExceptionMessage {

    GLOBAL_ERROR_ZUUL(INTERNAL_SERVER_ERROR.value(), "Gateway Internal Server Error", ServerErrorException.class),

    GLOBAL_REQUEST_NOT_FOUND(NOT_FOUND.value(), "Request not found", NotFoundException.class),

    GLOBAL_NOT_FOUND(NOT_FOUND.value(), "{} not found", NotFoundException.class),

    GLOBAL_RESOURCE_NOT_FOUND(NOT_FOUND.value(), "Resource not found", NotFoundException.class),

    GLOBAL_JSON_INVALID_FORMAT(BAD_REQUEST.value(), "Json invalid format", BadRequestException.class),

    GLOBAL_SWAGGER_JSON_INVALID_FORMAT(BAD_REQUEST.value(), "SwaggerJson invalid format", BadRequestException.class),

    GLOBAL_TIMEOUT(REQUEST_TIMEOUT.value(), REQUEST_TIMEOUT.getReasonPhrase(), TimeoutException.class),

    ACCESS_TOKEN_ALREADY_EXISTS(BAD_REQUEST.value(), "Access Token already used", BadRequestException.class),

    INTERCEPTOR_LIMIT_REACHED(BAD_REQUEST.value(), "Intercept limit reached", BadRequestException.class),

    INTERCEPTOR_INVALID_CONTENT(BAD_REQUEST.value(), "Content for {} interceptor is incorrect. Use the standard: {}", BadRequestException.class),

    INTERCEPTOR_NO_APP_FOUND(BAD_REQUEST.value(), "No App registered to this Api with client_id provided.", BadRequestException.class),

    INTERCEPTOR_NOT_EXIST(BAD_REQUEST.value(), "Interceptor defined not exist", BadRequestException.class),

    INTERCEPTOR_TEMPLATE_NOT_EXIST(BAD_REQUEST.value(), "Template interceptor not exist", BadRequestException.class),

    INTERCEPTOR_REFERENCE_NOT_FOUND(BAD_REQUEST.value(), "Reference interceptor not found", BadRequestException.class),

    INTERCEPTOR_IGNORED_INVALID(BAD_REQUEST.value(), "Reference operations invalid: {}", BadRequestException.class),

    INTERCEPTOR_INVALID_LIFECYCLE(BAD_REQUEST.value(), "{} interceptor can not be attached to the Plan life cycle", BadRequestException.class),

    MIDDLEWARE_NO_OPERATION_FOUND(BAD_REQUEST.value(), "Middleware must have an operation attached", BadRequestException.class),

    MIDDLEWARE_UNSUPPORTED_TYPE(BAD_REQUEST.value(), "File type differs from .jar not supported", BadRequestException.class),

    MIDDLEWARE_CONTAINS_INTERCEPTORS(BAD_REQUEST.value(), "Middleware still contains interceptors associated", BadRequestException.class),

    MIDDLEWARE_INVALID_FILE(BAD_REQUEST.value(), "Invalid file", BadRequestException.class),

    MIDDLEWARE_PAYLOAD_TOO_LARGE(PAYLOAD_TOO_LARGE.value(), "Content is to big. Maximal allowed request size is 25MB", MultipartException.class),

    ACCESS_TOKEN_NOT_FOUND(BAD_REQUEST.value(), "Access token not found", BadRequestException.class),

    APP_REPEATED(BAD_REQUEST.value(), "App repeated", BadRequestException.class),

    DEVELOPER_NOT_FOUND(BAD_REQUEST.value(), "Developer not found", BadRequestException.class),

    RESOURCE_METHOD_NOT_ACCEPT(BAD_REQUEST.value(), "method not accepted please use: GET, POST, PUT, PATH or DELETE", BadRequestException.class),

    APP_NOT_FOUND(BAD_REQUEST.value(), "App not found", BadRequestException.class),

    API_NOT_EXIST(BAD_REQUEST.value(), "Api does not exist", BadRequestException.class),

    API_BASEPATH_EXIST(BAD_REQUEST.value(), "The basepath defined exist", BadRequestException.class),

    API_BASEPATH_EMPTY(BAD_REQUEST.value(), "Basepath not defined", BadRequestException.class),

    API_CANT_ENVIRONMENT_INBOUND_URL_EQUALS(BAD_REQUEST.value(), "Apis can't have environments with the same inbound url", BadRequestException.class),

    ONLY_ONE_OPERATION_PER_RESOURCE(BAD_REQUEST.value(), "Only one operation per resource", BadRequestException.class),

    ONLY_ONE_RESOURCE_PER_API(BAD_REQUEST.value(), "Only one resource per api", BadRequestException.class),

    SOME_PLAN_NOT_PRESENT_IN_APP(BAD_REQUEST.value(), "Some of the informed plans do not belong to the App's plans", BadRequestException.class),

    ONLY_ONE_MIDDLEWARE_PER_VERSION_AND_API(BAD_REQUEST.value(), "Only one middleware per version and api", BadRequestException.class),

    ENVIRONMENT_ALREADY_EXISTS(BAD_REQUEST.value(), "Environment already exists", BadRequestException.class),

    PRIVILEGES_NOT_EXIST(BAD_REQUEST.value(), "Privileges {} defined to attach in role not exist ", BadRequestException.class),

    ACCESS_DENIED(UNAUTHORIZED.value(), "Access Denied", UnauthorizedException.class),

    ENVIRONMENT_ATTACHED_TO_API(BAD_REQUEST.value(), "Environment attached to Api", BadRequestException.class),

    PLAN_ATTACHED_TO_APPS(BAD_REQUEST.value(), "Plan attached to App", BadRequestException.class),

    ENVIRONMENT_INBOUND_DNS_PATTERN(BAD_REQUEST.value(), "Environment inbound URL has to follow the pattern http[s]://host.domain[:port] or www.host.domain[:port]", BadRequestException.class),

    PROVIDER_NOT_FOUND(BAD_REQUEST.value(), "Provider not found", BadRequestException.class),

    PROVIDER_USER_UNAUTHORIZED(UNAUTHORIZED.value(), "User provided unauthorized", UnauthorizedException.class),

    TOKEN_EXPIRED(UNAUTHORIZED.value(), "Token expired", UnauthorizedException.class),

    TOKEN_INVALID(FORBIDDEN.value(), "Token not valid", ForbiddenException.class),

    SIGNATURE_DOES_NOT_MATCH(UNAUTHORIZED.value(), "JWT signature does not match locally computed signature.", UnauthorizedException.class),

    TOKEN_NOT_GENERATE(FORBIDDEN.value(), "Error to generate token", ForbiddenException.class),

    CODE_NOT_FOUND(UNAUTHORIZED.value(), "Code already used to generate token or not defined", UnauthorizedException.class),

    GRANT_TYPE_NOT_FOUND(BAD_REQUEST.value(), "grant_type not found", BadRequestException.class),

    WRONG_GRANT_TYPE_INFORMED(BAD_REQUEST.value(), "Wrong grant_type informed", BadRequestException.class),

    REFRESH_TOKEN_NOT_FOUND(BAD_REQUEST.value(), "refresh_token not found", BadRequestException.class),

    TYPE_OAUTH_NOT_FOUND(BAD_REQUEST.value(), "TypeOAuth not found", BadRequestException.class),

    PRIVATE_KEY_NOT_FOUND(BAD_REQUEST.value(), "privateKey not found", BadRequestException.class),

    OPERATION_ATTACHED_TO_INTERCEPTOR(BAD_REQUEST.value(), "Operation attached to Interceptor", BadRequestException.class),

    OPERATION_CANT_HAVE_SINGLE_WILDCARD(BAD_REQUEST.value(), "Operation can not have a single wild card (/*)", BadRequestException.class),

    OPERATION_CANT_HAVE_DOUBLE_WILDCARD_NOT_AT_THE_END(BAD_REQUEST.value(), "Operation can have a double wild card (/**), but only at the end", BadRequestException.class),

    OPERATION_ROUTE_ALREADY_EXISTS(BAD_REQUEST.value(), "A Operation with the same route exists in another Api.", BadRequestException.class),

    API_BASEPATH_MALFORMED(BAD_REQUEST.value(), "Api basepath can not contain a wild card", BadRequestException.class),

    USERNAME_OR_PASSWORD_INCORRECT(BAD_REQUEST.value(), "Username or password incorrect", BadRequestException.class),

    USERNAME_ALREADY_EXIST(BAD_REQUEST.value(), "Username already exist!", BadRequestException.class),

    EMAIL_ALREADY_EXIST(BAD_REQUEST.value(), "Email already exist!", BadRequestException.class),

    APP_CLIENT_ID_ALREADY_USED(BAD_REQUEST.value(), "Client Id already used", BadRequestException.class),

    APP_CLIENT_ID_NOT_FOUND(BAD_REQUEST.value(), "Client Id not found", BadRequestException.class),

    AUTHORIZATION_NOT_FOUND(UNAUTHORIZED.value(), "Authorization not found in header", UnauthorizedException.class),

    RESPONSE_TYPE_NOT_FOUND(BAD_REQUEST.value(), "response_type not found", BadRequestException.class),

    DEFAULT_PROVIDER_CAN_NOT_UPDATED_OR_REMOVED(FORBIDDEN.value(), "Default Provider can't to be updated or removed!", ForbiddenException.class),

    ROLE_ALREADY_EXIST(BAD_REQUEST.value(), "Role already exist!", BadRequestException.class),

    SCOPE_INVALID_OPERATION(BAD_REQUEST.value(), "Operation with id '{}' does not exist", BadRequestException.class),

    SCOPE_INVALID_PLAN(BAD_REQUEST.value(), "Plan id with '{}' does not exist", BadRequestException.class),

    SCOPE_OPERATION_NOT_IN_API(BAD_REQUEST.value(), "Operation '{}' not in Api '{}'", BadRequestException.class),

    SCOPE_PLAN_NOT_IN_API(BAD_REQUEST.value(), "Plan '{}' not in Api '{}'", BadRequestException.class),

    SCOPE_INVALID_NAME(BAD_REQUEST.value(), "A Scope with the provided name already exists", BadRequestException.class),

    SCOPE_NO_OPERATION_FOUND(BAD_REQUEST.value(), "A Scope must have at least one Operation", BadRequestException.class),

    CORS_INTERCEPTOR_NOT_API_LIFE_CYCLE(BAD_REQUEST.value(), "The CORS Interceptor only allowed for API LifeCycle", BadRequestException.class),

    CORS_INTERCEPTOR_ALREADY_ASSIGNED_TO_THIS_API(BAD_REQUEST.value(), "A CORS Interceptor already assigned to this API", BadRequestException.class),

    USER_NEW_PASSWORD_EQUALS_CURRENT_PASSWORD(BAD_REQUEST.value(), "New password must different from the current password!", BadRequestException.class),

    USER_CURRENT_PASSWORD_NOT_MATCHING(BAD_REQUEST.value(), "Current password not matching!", BadRequestException.class),

    USER_NEW_PASSWORD_NOT_MATCHING(BAD_REQUEST.value(), "New password not matching!", BadRequestException.class),

    USER_UNAUTHORIZED_TO_CHANGE_PASSWORD(UNAUTHORIZED.value(), "You can't change the password!", UnauthorizedException.class),

    USER_LDAP_UNAUTHORIZED_TO_CHANGE_PASSWORD(UNAUTHORIZED.value(), "User from LDAP can't change the password!", UnauthorizedException.class),

    DEFAULT_PLAN_ALREADY_EXIST_TO_THIS_API(BAD_REQUEST.value(), "Default plan already exist to this Api", BadRequestException.class);

    @Getter
    private final Integer httpCode;

    @Getter
    @Setter
    private String message;

    private final String defaultMessage;

    @Getter
    private final Class<? extends HeimdallException> klass;

    ExceptionMessage(int httpCode, String message, Class<? extends HeimdallException> klass) {

        this.httpCode = httpCode;
        this.defaultMessage = message;
        this.klass = klass;
        this.message = (this.message == null || this.message.isEmpty()) ? this.defaultMessage.replace("{}", "") : this.message;
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

        this.message = (this.message == null || this.message.isEmpty()) ? this.defaultMessage.replace("{}", "") : this.message;

        switch(HttpStatus.valueOf(this.httpCode)){
            case BAD_REQUEST:
                throw new BadRequestException(this);
            case UNAUTHORIZED:
                throw new UnauthorizedException(this);
            case FORBIDDEN:
                throw new ForbiddenException(this);
            case NOT_FOUND:
                throw new NotFoundException(this);
            case REQUEST_TIMEOUT:
                throw new TimeoutException(this);
            default:
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

            if (dynamicText.length == 1) {
                this.message = messageDefault.replace("{}", dynamicText[0]);
            } else {

                int count = 0;
                while (messageDefault.contains("{}") && count < dynamicText.length) {
                    messageDefault = messageDefault.replaceFirst("\\{}", dynamicText[count]);

                    this.message = messageDefault;
                    count++;
                }
            }
        }
        raise();
    }

}
