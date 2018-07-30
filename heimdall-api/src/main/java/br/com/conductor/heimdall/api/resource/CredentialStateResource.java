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
import br.com.conductor.heimdall.api.service.CredentialStateService;
import br.com.conductor.heimdall.core.util.ConstantsPath;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Uses a {@link CredentialStateService} to provide methods to create, read, update and delete a {@link CredentialState}
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@RestController
public class CredentialStateResource {

    @Autowired
    private CredentialStateService credentialStateService;

    @ResponseBody
    @ApiOperation(value = "Change credential state to LOGOUT", response = String.class)
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