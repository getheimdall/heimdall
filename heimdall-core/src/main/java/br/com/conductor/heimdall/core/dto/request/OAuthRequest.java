package br.com.conductor.heimdall.core.dto.request;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
public class OAuthRequest implements Serializable{
	
	private static final long serialVersionUID = 6985307454557101510L;

	@NotNull(message = "client_id needs to be informed.")
	private String client_id;
	
	private String grant_type;
	
	private Set<String> operations;

	private String refresh_token;
	
	private String code;
}
