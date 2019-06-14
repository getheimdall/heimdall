package br.com.conductor.heimdall.core.interceptor.impl;

import br.com.conductor.heimdall.core.dto.interceptor.LogWriterDTO;
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
public class LogWriterHeimdallInterceptor implements HeimdallInterceptor {

    @Override
    public String getFile(TypeExecutionPoint typeExecutionPoint) {
        return "log_writer.mustache";
    }

    @Override
    public LogWriterDTO parseContent(String content) {
        try {
            return JsonUtils.convertJsonToObject(content, LogWriterDTO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionMessage.INTERCEPTOR_INVALID_CONTENT.raise(TypeInterceptor.LOG_WRITER.name(), TemplateUtils.TEMPLATE_LOG_WRITER);
        }
        return null;
    }

    @Override
    public Map<String, Object> buildParameters(Interceptor interceptor) {

        Map<String, Object> parameters = new HashMap<>();
        LogWriterDTO logWriterDTO = this.parseContent(interceptor.getContent());

        parameters.put("body", logWriterDTO.getBody());
        parameters.put("headers", logWriterDTO.getHeaders());
        parameters.put("requiredHeaders", logWriterDTO.getRequiredHeaders());

        return parameters;
    }
}
