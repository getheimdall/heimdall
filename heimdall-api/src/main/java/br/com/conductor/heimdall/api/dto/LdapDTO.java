package br.com.conductor.heimdall.api.dto;

import br.com.conductor.heimdall.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LdapDTO implements Serializable {

    private Long id;

    @NotNull(message = "URL needs to be informed.")
    @Size(max = 200)
    private String url;

    @NotNull(message = "SearchBase needs to be informed.")
    private String searchBase;

    @NotNull(message = "UserDn needs to be informed.")
    @Size(max = 100)
    private String userDn;

    @NotNull(message = "Password needs to be informed.")
    private String password;

    @NotNull(message = "UserSearchFilter needs to be informed.")
    @Size(max = 120)
    private String userSearchFilter;

    @NotNull(message = "Status needs to be informed.")
    private Status status;
}
