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
