package br.com.conductor.heimdall.middleware.spec;

public interface Info {
     
     public String appName();

     public String apiName();

     public Long apiId();
     
     public String developer();
     
     public String method();

     public String clientId();

     public String accessToken();

     public String pattern();

     public Long operationId();

     public String profile();
     
     public Long resourceId();

     public String url();

     public String requestURI();

}
