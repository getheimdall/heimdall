package br.com.conductor.heimdall.core.entity;

import java.util.List;

import br.com.conductor.heimdall.core.trace.FilterDetail;
import br.com.conductor.heimdall.core.trace.GeneralTrace;
import br.com.conductor.heimdall.core.trace.RequestResponseParser;
import br.com.conductor.heimdall.core.trace.StackTrace;
import lombok.Data;

@Data
public class Trace {
	
    private String method;

    private String url;

    private Integer resultStatus;

    private Long durationMillis;

    private String insertedOnDate;

    private Long apiId;

    private String apiName;

    private String app;

    private String accessToken;

    private String receivedFromAddress;

    private String clientId;

    private Long resourceId;

    private String appDeveloper;
    
    private Long operationId;
    
    private RequestResponseParser request;
    
    private RequestResponseParser response;

    private String pattern;
    
    private StackTrace stackTrace;

    private List<GeneralTrace> traces;
    
    private List<FilterDetail> filters;

    private String profile;

    private Boolean cache;

}
