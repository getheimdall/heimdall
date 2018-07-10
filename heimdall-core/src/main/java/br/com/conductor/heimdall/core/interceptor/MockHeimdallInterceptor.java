package br.com.conductor.heimdall.core.interceptor;

import br.com.conductor.heimdall.core.dto.interceptor.MockDTO;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interfaces.HeimdallInterceptor;

import java.util.HashMap;

/**
 * Implementation of the HeimdallInterceptor to type Mock.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class MockHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "mock.mustache";
    }

    @Override
    public boolean validateTemplate(Object objectCustom) {
        return false;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters) {
        MockDTO mockDTO = (MockDTO) objectCustom;
        parameters.put("status", mockDTO.getStatus());
        parameters.put("body", mockDTO.getBody());
        return parameters;
    }
}
