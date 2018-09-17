/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.logs.FiltersDTO;
import br.com.conductor.heimdall.core.dto.logs.LogTraceDTO;
import br.com.conductor.heimdall.core.dto.page.LogTraceDTOPage;
import br.com.conductor.heimdall.core.entity.LogTrace;
import br.com.conductor.heimdall.core.entity.Trace;
import br.com.conductor.heimdall.core.util.MongoLogConnector;
import br.com.conductor.heimdall.core.util.Page;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to read the (@link LogTrace} resource.
 * 
 * @author Marcelo Aguiar
 *
 */
@Service
public class TraceService {
	
	@Autowired
	private MongoLogConnector mongoConnection;

    /**
     * Finds a {@link Trace} by its Id.
     * 
     * @param id The Trace Id
     * @return Trace found
     */
    @Transactional(readOnly = true)
	public LogTraceDTO findById(String id) {
		ObjectId oid = new ObjectId(id);
		LogTrace object = new LogTrace();
		object.setId(oid);
		LogTrace logTrace = mongoConnection.findOne(object);

		return new LogTraceDTO(logTrace);
	}

    /**
     * Creates a paged list of traces from the filters provided
     *
     * @param filtersDTOS List of filters
     * @param pageableDTO Paging parameters
     * @return Paged list of traces
     */
	public LogTraceDTOPage find(List<FiltersDTO> filtersDTOS, PageableDTO pageableDTO) {

        Page<LogTrace> page = mongoConnection.find(filtersDTOS, pageableDTO.getOffset(), pageableDTO.getLimit());
        Page<LogTraceDTO> response = createPagedResponse(page);

        return new LogTraceDTOPage(PageDTO.build(response));
    }

 	/*
 	 * Method to transform a Page<LogTrace> into a Page<LogTraceDTO>.
 	 * The reason for this is that the id field from the mongo response
 	 * is a ObjectId, and the response to the Api should be the string
 	 * representation of that object.
 	 */
 	private Page<LogTraceDTO> createPagedResponse(Page<LogTrace> page) {

        Page<LogTraceDTO> p = new Page<>();

        p.first = page.first;
        p.hasContent = page.hasContent;
        p.hasNextPage = page.hasNextPage;
        p.hasPreviousPage = page.hasPreviousPage;
        p.last = page.last;
        p.nextPage = page.nextPage;
        p.number = page.number;
        p.numberOfElements = page.numberOfElements;
        p.previousPage = page.previousPage;
        p.totalElements = page.totalElements;
        p.totalPages = page.totalPages;

        List<LogTraceDTO> logTraces = new ArrayList<>();

	    page.content.forEach(logTrace -> logTraces.add(new LogTraceDTO(logTrace)));

	    p.content = logTraces;

	    return p;
    }
}
