package br.com.conductor.heimdall.core.dto.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.conductor.heimdall.core.dto.ReferenceIdDTO;
import br.com.conductor.heimdall.core.enums.Status;
import lombok.Data;

@Data
public class AppRequestDTO implements Serializable {

     private static final long serialVersionUID = 2289226137585901893L;

     @NotNull
     @Size(max = 180)
     private String name;

     @Size(max = 200)
     private String description;
     
     @NotNull
     private ReferenceIdDTO developer;

     private List<String> tags;
     
     private Status status;
     
     private String clientId;
}
