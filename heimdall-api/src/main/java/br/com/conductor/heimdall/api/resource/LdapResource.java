/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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
package br.com.conductor.heimdall.api.resource;

import br.com.conductor.heimdall.api.dto.LdapDTO;
import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.api.service.LdapService;
import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Objects;

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_LDAP;

@io.swagger.annotations.Api(value = PATH_LDAP, produces = MediaType.APPLICATION_JSON_VALUE, tags = { ConstantsTag.TAG_LDAP})
@RestController
@RequestMapping(PATH_LDAP)
public class LdapResource {

    @Autowired
    private LdapService ldapService;

    @ApiOperation("Update settings of the LDAP")
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_UPDATE_LDAP)
    public ResponseEntity update(@RequestBody @Valid LdapDTO ldapDTO) {
        Ldap ldap = ldapService.save(ldapDTO);

        if (Objects.nonNull(ldap)) {
            return ResponseEntity.ok().body(ldap);
        }

        return ResponseEntity.notFound().build();
    }

    @ApiOperation("Get settings of the LDAP")
    @GetMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_LDAP)
    public ResponseEntity getLdap() {
        Ldap ldap = ldapService.getLdap();
        if (Objects.nonNull(ldap)) {
            return ResponseEntity.ok(ldap);
        }

        return ResponseEntity.notFound().build();
    }
}
