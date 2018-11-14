package br.com.conductor.heimdall.api.resource;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import br.com.conductor.heimdall.api.entity.CredentialState;
import br.com.conductor.heimdall.api.security.AccountCredentials;
import br.com.conductor.heimdall.api.service.CredentialStateService;
import br.com.conductor.heimdall.api.service.TokenAuthenticationService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_API;

/**
 * Uses a {@link CredentialStateService} and {@link TokenAuthenticationService} to provide methods to update and delete
 * a {@link CredentialState} and provider authentication.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@io.swagger.annotations.Api(value = PATH_API, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_AUTH })
@RestController
public class AuthResource {

    @Autowired
    private CredentialStateService credentialStateService;

    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;

    @ApiOperation(value = "Login Authentication")
    @PostMapping(ConstantsPath.PATH_LOGIN)
    public void login(@RequestBody AccountCredentials accountCredentials, HttpServletResponse response) {
        tokenAuthenticationService.login(accountCredentials, response);
    }

    @ResponseBody
    @ApiOperation(value = "Logout Authentication", response = String.class)
    @GetMapping(ConstantsPath.PATH_LOGOUT)
    public ResponseEntity logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            token = token.replace("Bearer ", "");
            credentialStateService.logout(token);
            return ResponseEntity.ok().body("Logout with success.");
        }

        return ResponseEntity.badRequest().body("Authorization not found in Header");
    }
}
