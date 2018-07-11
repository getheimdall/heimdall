package br.com.conductor.heimdall.core.interceptor;

import br.com.conductor.heimdall.core.dto.interceptor.IpsDTO;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interfaces.HeimdallInterceptor;

import java.util.HashMap;

/**
 * Implementation of the HeimdallInterceptor to type Whitelist.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class WhitelistHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "whitelist_ip.mustache";
    }

    @Override
    public boolean validateTemplate(Object objectCustom) {
        return false;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters) {
        IpsDTO ipsDTO = (IpsDTO) objectCustom;
        parameters.put("ips", ipsDTO.getIps());
        return parameters;
    }
}
