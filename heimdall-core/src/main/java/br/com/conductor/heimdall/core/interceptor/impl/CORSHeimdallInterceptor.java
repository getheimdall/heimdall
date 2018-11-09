package br.com.conductor.heimdall.core.interceptor.impl;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.interceptor.HeimdallInterceptor;
import br.com.conductor.heimdall.core.util.JsonUtils;
import br.com.conductor.heimdall.core.util.TemplateUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CORSHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "cors-pre.mustache";
    }

    @Override
    public Object parseContent(String content) {
        try {
            Map<String, String> map = (Map<String, String>) JsonUtils.convertJsonToObject(content, Map.class);
            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(TypeInterceptor.CORS.name(), TemplateUtils.TEMPLATE_CORS);
        }

        return null;
    }

    @Override
    public HashMap<String, Object> buildParameters(Object objectCustom, HashMap<String, Object> parameters, Interceptor interceptor) {

        Map<String, String> cors = (Map<String, String>) objectCustom;
        parameters.put("cors", cors);

        return parameters;
    }
}
