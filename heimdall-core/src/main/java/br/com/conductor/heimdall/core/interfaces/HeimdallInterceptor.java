package br.com.conductor.heimdall.core.interfaces;

import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;

import java.util.HashMap;

public interface HeimdallInterceptor {

    String getFile(TypeExecutionPoint typeExecutionPoint);

    boolean validateTemplate(Object objectCustom);

    HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters);
}
