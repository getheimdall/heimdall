package br.com.conductor.heimdall.core.entity;

import lombok.Data;

import java.util.Map;

@Data
public class ApiResponse {

    private String body;

    private Map<String, String> headers;

    private Integer status;
}
