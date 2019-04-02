package br.com.conductor.heimdall.gateway.router;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentInfo implements Serializable {

    private Long id;
    private String outboundURL;
    private Map<String, String> variables;

}
