package br.com.conductor.heimdall.core.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 *
 */
@Data
public class ProviderDTO implements Serializable{

	private static final long serialVersionUID = 6270561769116836980L;
	
	@NotNull
	private String name;
    
    private String description;

    @NotNull
    private String path;
    
    @NotNull
    private List<ProviderParamsDTO> providerParams;
    
    @JsonIgnore
    private LocalDateTime creationDate;
    
    @JsonIgnore
    private Status status = Status.ACTIVE;
    
}
