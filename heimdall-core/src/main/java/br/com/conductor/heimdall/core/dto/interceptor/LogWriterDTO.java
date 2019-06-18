package br.com.conductor.heimdall.core.dto.interceptor;

import lombok.Data;

import java.util.List;

@Data
public class LogWriterDTO {

    private Boolean body = false;

    private Boolean headers = false;

    private List<String> requiredHeaders = null;

}
