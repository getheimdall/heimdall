package br.com.conductor.heimdall.gateway.router;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credential implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5161963589199384852L;
	private String method;
	private String operationPath;
	private String apiBasePath;
	private String apiName;
	private long operationId;
	private long resourceId;
	private long apiId;
	private boolean cors;
	
}
