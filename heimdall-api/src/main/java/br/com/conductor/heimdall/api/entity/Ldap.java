package br.com.conductor.heimdall.api.entity;

import br.com.conductor.heimdall.core.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Table(name = "LDAP")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ldap implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1018313339857163210L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "URL", length = 200, nullable = false)
    private String url;

    @Column(name = "SEARCH_BASE", length = 200, nullable = false)
    private String searchBase;

    @Column(name = "USER_DN", length = 100, nullable = false)
    private String userDn;

    @Column(name = "PASSWORD", length = 100, nullable = false)
    private String password;

    @Column(name = "USER_SEARCH_FILTER", length = 120, nullable = false)
    private String userSearchFilter;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private Status status;
}
