package br.com.conductor.heimdall.core.interceptor;

import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interfaces.HeimdallInterceptor;

import java.util.HashMap;

import static br.com.conductor.heimdall.core.util.Constants.MIDDLEWARE_API_ROOT;
import static br.com.conductor.heimdall.core.util.Constants.MIDDLEWARE_ROOT;

/**
 * Implementation of the HeimdallInterceptor to type Middleware.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class MiddlewareHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "middleware.mustache";
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
