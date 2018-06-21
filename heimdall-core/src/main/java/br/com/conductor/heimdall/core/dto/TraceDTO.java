package br.com.conductor.heimdall.core.dto;

import java.io.Serializable;

import br.com.conductor.heimdall.core.enums.HttpMethod;
import lombok.Data;

@Data
public class TraceDTO implements Serializable {
	
	private static final long serialVersionUID = -8264906278477847182L;

	private HttpMethod method;
	
	private Integer resultStatus;
	
	private String url;
	
	private String insertedOnDate;
	
}
