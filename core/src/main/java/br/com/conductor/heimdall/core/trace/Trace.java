/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.conductor.heimdall.core.trace;

import static net.logstash.logback.marker.Markers.append;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import br.com.conductor.heimdall.core.exception.ExceptionMessage;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.util.UrlUtil;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents the trace message.
 *
 * @author Thiago Sampaio
 *
 */
@Data
@Slf4j
@JsonInclude(Include.NON_NULL)
@JsonFilter("customFilter")
public class Trace {

    private static final Logger logstash = LoggerFactory.getLogger("logstash");

    private String method;

    private String url;

    private int resultStatus;

    @JsonIgnore
    private Long initialTime;

    private Long durationMillis;

    private String apiId;

    private String apiName;

    private String app;

    private String accessToken;

    private String receivedFromAddress;

    private String clientId;

    private String resourceId;

    private String appDeveloper;

    private String operationId;

    private RequestResponseParser request;

    private RequestResponseParser response;

    private Boolean cache;

    @Getter
    private List<GeneralTrace> traces;

    @Getter
    private Map<String, FilterDetail> filters;

    private String profile;

    @JsonIgnore
    private boolean printAllTrace;

    @JsonIgnore
    private boolean shouldPrint;

    @JsonIgnore
    private boolean printLogstash;

    @JsonIgnore
    private boolean printFilters;

    public Trace() {

    }

    /**
     * Create a Trace.
     * @param printAllTrace
     * @param profile
     * @param servletRequest
     * @param printLogstash
     */
    public Trace(boolean printAllTrace, String profile, ServletRequest servletRequest, boolean printLogstash, boolean printFilters){

        this.shouldPrint = true;
        this.profile = profile;
        this.printAllTrace = printAllTrace;
        this.printLogstash = printLogstash;
        this.printFilters = printFilters;

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HeimdallException.checkThrow(request == null, ExceptionMessage.GLOBAL_REQUEST_NOT_FOUND);

        this.initialTime = System.currentTimeMillis();
        this.method = request.getMethod();
        this.url = UrlUtil.getCurrentUrl(request);

        Enumeration<String> headers = request.getHeaders("x-forwarded-for");

        if (headers != null) {
            List<String> listIps = Collections.list(headers);
            String received = String.join(",", listIps);
            this.receivedFromAddress = "".equals(received) ? null : received;
        }

    }

    /**
     * Adds a {@link FilterDetail} to the List.
     *
     * @param detail {@link FilterDetail}
     */
    public void addFilter(String name, FilterDetail detail) {
        if (this.filters == null) this.filters = new LinkedHashMap<>();

        filters.put(name, detail);
    }

    /**
     * Creates and adds a new trace to the traces List.
     *
     * @param msg	Message to be added to the trace
     * @return		{@link Trace} created
     */
    public Trace trace(String msg) {

        return this.trace(msg, null);
    }

    /**
     * Creates and adds a new trace from message and Object.
     *
     * @param msg		The message for the trace
     * @param object	The Object to be added to the trace
     * @return			{@link Trace} created
     */
    public Trace trace(String msg, Object object) {

        if (this.traces == null) this.traces = new ArrayList<>();

        traces.add(new GeneralTrace(msg, object));

        return this;
    }

    /**
     * Writes a {@link HttpServletResponse} to the Heimdall Trace
     *
     * @param response	{@link HttpServletResponse}
     */
    public void write(HttpServletResponse response) {

        try {

            this.resultStatus = response.getStatus();
            this.durationMillis = System.currentTimeMillis() - getInitialTime();

            if (!this.profile.equals("developer")) {
                this.filters = null;
            }

            ObjectMapper mapper = mapper();

            if (this.printAllTrace) {
            	MessageDelegate.send(log, this.resultStatus, " [HEIMDALL-TRACE] - " + mapper.writeValueAsString(this));
            } else {
                String url = Objects.nonNull(this.url) ? this.url : "";
                MessageDelegate.send(log, this.resultStatus, append("call", this), " [HEIMDALL-TRACE] - " + url);
            }

            if (this.printLogstash) {
            	MessageDelegate.send(logstash, this.resultStatus, append("trace", mapper.convertValue(this, Map.class)), null);
            }
        } catch (Exception e) {

            log.error(e.getMessage(), e);
        } finally {

            TraceContextHolder.getInstance().clearActual();
        }

    }

    private ObjectMapper mapper() {
        SimpleBeanPropertyFilter customFilter;

        if (!this.printFilters) {
            customFilter = SimpleBeanPropertyFilter.serializeAllExcept("filters");
        } else {
            customFilter = SimpleBeanPropertyFilter.serializeAll();
        }

        FilterProvider filters = new SimpleFilterProvider().addFilter("customFilter", customFilter);

        return new ObjectMapper().setFilterProvider(filters);
    }
}
