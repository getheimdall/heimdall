package br.com.conductor.heimdall.api.service;

import br.com.conductor.heimdall.api.dto.LdapDTO;
import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.api.repository.LdapRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LdapService {

    @Autowired
    private LdapRepository ldapRepository;

    public Ldap save(LdapDTO ldapDTO){

        Ldap ldap;

        if (Objects.nonNull(ldapDTO.getId())) {
            Ldap ldapFound = ldapRepository.findOne(ldapDTO.getId());
            ldap = GenericConverter.mapper(ldapDTO, ldapFound);
        } else {
            ldap = GenericConverter.mapper(ldapDTO, Ldap.class);
        }

        return ldapRepository.save(ldap);
    }

    public Ldap getLdap() {
        return this.ldapRepository.findOne(1L);
    }

    public Ldap getLdapActive() {
        return this.ldapRepository.findByStatus(Status.ACTIVE);
    }
}
