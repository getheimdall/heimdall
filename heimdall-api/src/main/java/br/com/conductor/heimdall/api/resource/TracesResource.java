package br.com.conductor.heimdall.api.resource;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 * ==========================LICENSE_END===================================
 */

import static br.com.conductor.heimdall.core.util.ConstantsPath.PATH_TRACES;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.conductor.heimdall.api.util.ConstantsPrivilege;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.logs.FiltersDTO;
import br.com.conductor.heimdall.core.dto.logs.LogTraceDTO;
import br.com.conductor.heimdall.core.dto.page.LogTraceDTOPage;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.conductor.heimdall.core.service.TraceService;
import br.com.conductor.heimdall.core.util.ConstantsTag;
import io.swagger.annotations.ApiOperation;

/**
 * Uses a LogTraceService to provide access to the Log Traces
 *
 * @author Marcelo Aguiar
 */
@io.swagger.annotations.Api(value = PATH_TRACES, produces = MediaType.APPLICATION_JSON_VALUE, tags = {ConstantsTag.TAG_TRACES})
@RestController
@RequestMapping(value = PATH_TRACES)
public class TracesResource {

    @Autowired
    private TraceService traceService;
    
    @Autowired
    private Property property;

    /**
     * Returns a one LogTrace by its id
     *
     * @return {@link ResponseEntity}
     */
    @ResponseBody
    @ApiOperation(value = "Find Trace by id", response = LogTraceDTO.class)
    @GetMapping(value = "/{traceId}")
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_TRACES)
    public ResponseEntity<?> findOne(@PathVariable("traceId") String id) {
    	
    	if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());
    	
        LogTraceDTO logTrace = traceService.findById(id);

        return ResponseEntity.ok(logTrace);
    }

    /**
     * Filters the traces by the parameters provided
     *
     * @param filtersSelected List of filters
     * @param pageableDTO     Paging parameters
     * @return Paged list of traces
     */
    @ResponseBody
    @ApiOperation(value = "Find Traces", responseContainer = "List", response = LogTraceDTO.class)
    @PostMapping
    @PreAuthorize(ConstantsPrivilege.PRIVILEGE_READ_TRACES)
    public ResponseEntity<?> find(@RequestBody List<FiltersDTO> filtersSelected, @ModelAttribute PageableDTO pageableDTO) {
    	
    	if (!property.getMongo().getEnabled()) return ResponseEntity.ok(new JSONObject().toString());

        LogTraceDTOPage logTrace = traceService.find(filtersSelected, pageableDTO);

        return ResponseEntity.ok(logTrace);
    }
}
