package br.com.conductor.heimdall.api.repository;

import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LDAP Repository.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public interface LdapRepository extends JpaRepository<Ldap, Long> {

    Ldap findByStatus(Status status);
}
