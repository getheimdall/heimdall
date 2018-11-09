package br.com.conductor.heimdall.core.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
public class ScopeDTO implements Serializable {

    @NotNull
    @Size(max = 180)
    private String name;

    @Size(max = 200)
    private String description;

}
