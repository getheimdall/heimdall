package br.com.conductor.heimdall.core.interceptor;

import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interfaces.HeimdallInterceptor;

import java.util.HashMap;

/**
 * Implementation of the HeimdallInterceptor to type Log.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class LogHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {

        if (typeExecutionPoint.equals(TypeExecutionPoint.FIRST)) {
            return "request_log.mustache";
        }

        return "response_log.mustache";
    }

    @Override
    public boolean validateTemplate(Object objectCustom) {
        return false;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters) {
        return null;
    }
}
