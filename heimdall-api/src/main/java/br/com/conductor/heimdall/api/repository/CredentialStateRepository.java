package br.com.conductor.heimdall.api.repository;

import br.com.conductor.heimdall.api.entity.CredentialState;
import br.com.conductor.heimdall.api.enums.CredentialStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides methods to access a {@link CredentialState}.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public interface CredentialStateRepository extends JpaRepository<CredentialState, Long> {

    CredentialState findByJtiAndStateEquals(String jti, CredentialStateEnum credentialStateEnum);

    CredentialState findByJti(String jti);

    CredentialState findByUsername(String username);
}
