package br.com.conductor.heimdall.core.interceptor;

import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interfaces.HeimdallInterceptor;

import java.util.HashMap;

/**
 * Implementation of the HeimdallInterceptor to type Custom.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class CustomHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "custom.mustache";
    }

    @Override
    public boolean validateTemplate(Object objectCustom) {
        return false;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters) {
        parameters.put("content", objectCustom);

        return parameters;
    }
}
