package br.com.conductor.heimdall.api.dto;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Data transference object class that represents a Heimdall user.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
public class UserEditDTO implements Serializable {

    private static final long serialVersionUID = 1870453965792235839L;

    @NotNull
    @Size(max = 80)
    private String firstName;

    @NotNull
    @Size(max = 80)
    private String lastName;

    @NotNull
    @Size(max = 80)
    @Email
    private String email;

    @NotNull
    @Size(max = 30, min=5)
    private String userName;

    private Status status;

    private List<ReferenceIdDTO> roles;
}
