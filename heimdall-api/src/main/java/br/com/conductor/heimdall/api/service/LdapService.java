/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.api.service;

import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.api.repository.LdapRepository;
import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Provides methods to find one or mode {@link Ldap}.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a
 */
@Service
public class LdapService {

    @Autowired
    private LdapRepository ldapRepository;

    public Ldap save(Ldap ldap){


        if (Objects.nonNull(ldap.getId())) {
            Ldap ldapFound = ldapRepository.findOne(ldap.getId());
            ldap = GenericConverter.mapper(ldap, ldapFound);
        } else {
            ldap = GenericConverter.mapper(ldap, Ldap.class);
        }

        return ldapRepository.save(ldap);
    }

    /**
     * Finds a {@link Ldap}
     *
     * @return The {@link Ldap}
     */
    public Ldap getLdap() {
        return this.ldapRepository.findAll().stream().findFirst().orElse(null);
    }

    /**
     * Finds a {@link Ldap} with status ACTIVE
     *
     * @return The {@link Ldap}
     */
    public Ldap getLdapActive() {
        return this.ldapRepository.findByStatus(Status.ACTIVE);
    }
}
