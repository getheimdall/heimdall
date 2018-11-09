package br.com.conductor.heimdall.core.repository;

import br.com.conductor.heimdall.core.entity.Scope;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScopeRepository extends JpaRepository<Scope, Long> {

    Scope findByApiIdAndId(Long apiId, Long id);

    Scope findByApiIdAndName(Long apiId, String name);

}
