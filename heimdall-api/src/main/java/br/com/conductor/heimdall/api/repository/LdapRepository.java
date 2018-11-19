package br.com.conductor.heimdall.api.repository;

import br.com.conductor.heimdall.api.entity.Ldap;
import br.com.conductor.heimdall.core.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LdapRepository extends JpaRepository<Ldap, Long> {

    Ldap findByStatus(Status status);
}
