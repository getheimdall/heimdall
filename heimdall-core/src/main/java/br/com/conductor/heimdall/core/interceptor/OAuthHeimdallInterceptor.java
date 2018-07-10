package br.com.conductor.heimdall.core.interceptor;

import br.com.conductor.heimdall.core.dto.interceptor.OAuthDTO;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.interfaces.HeimdallInterceptor;
import br.com.twsoftware.alfred.object.Objeto;

import java.util.HashMap;

/**
 * Implementation of the HeimdallInterceptor to type OAuth.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
public class OAuthHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "oauth.mustache";
    }

    @Override
    public boolean validateTemplate(Object objectCustom) {
        return false;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters) {
        OAuthDTO oAuthDTO = (OAuthDTO) objectCustom;

        parameters.put("providerId", Objeto.isBlank(oAuthDTO.getProviderId()) ? 0L : oAuthDTO.getProviderId());
        parameters.put("timeAccessToken", Objeto.isBlank(oAuthDTO.getTimeAccessToken()) ? 20 : oAuthDTO.getTimeAccessToken());
        parameters.put("timeRefreshToken", Objeto.isBlank(oAuthDTO.getTimeRefreshToken()) ? 1800 : oAuthDTO.getTimeRefreshToken());
        parameters.put("typeOAuth", oAuthDTO.getTypeOAuth());
        parameters.put("privateKey", oAuthDTO.getPrivateKey());

        return parameters;
    }
}
