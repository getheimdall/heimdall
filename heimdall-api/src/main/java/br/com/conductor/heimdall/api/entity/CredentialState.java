package br.com.conductor.heimdall.api.entity;

import br.com.conductor.heimdall.api.enums.CredentialStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CREDENTIAL_STATE")
@Entity
public class CredentialState implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "JTI", length = 50, nullable = false, unique = true)
    private String jti;

    @Column(name = "USERNAME", length = 30, nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE", length = 20, nullable = false)
    private CredentialStateEnum state;
}
