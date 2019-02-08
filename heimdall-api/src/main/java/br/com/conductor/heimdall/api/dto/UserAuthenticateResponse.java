package br.com.conductor.heimdall.api.dto;

import br.com.conductor.heimdall.api.entity.Privilege;
import br.com.conductor.heimdall.api.enums.TypeUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class UserAuthenticateResponse implements Serializable {

    private String username;
    private TypeUser type;
    private Set<Privilege> privileges;
}
