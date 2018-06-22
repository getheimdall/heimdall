
package br.com.conductor.heimdall.core.service;

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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.conductor.heimdall.core.dto.LogTraceDTO;
import br.com.conductor.heimdall.core.dto.page.LogTraceDTOPage;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.conductor.heimdall.core.dto.TraceDTO;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.entity.LogTrace;
import br.com.conductor.heimdall.core.entity.Trace;
import br.com.conductor.heimdall.core.util.MongoLogConnector;
import br.com.conductor.heimdall.core.util.Page;
import br.com.twsoftware.alfred.object.Objeto;

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
	
	public List<LogTraceDTO> findAll() {
		List<LogTrace> logTraces = mongoConnection.findAll();

		List<LogTraceDTO> logTraceDTOS = new ArrayList<>();

		logTraces.forEach(logTrace -> logTraceDTOS.add(new LogTraceDTO(logTrace)));

		return logTraceDTOS;
	}
	
	/**
	 * Generates a paged list of the {@link Trace} saved.
	 * 
	 * @param traceDTO Trace DTO
	 * @param pageableDTO Pageable DTO
	 * @return
	 */
    @Transactional(readOnly = true)
	public LogTraceDTOPage list(TraceDTO traceDTO, PageableDTO pageableDTO) {
		
		Map<String, Object> query = prepareQuery(traceDTO);

		Page<LogTrace> page = mongoConnection.find(query, pageableDTO.getOffset(), pageableDTO.getLimit());
        Page<LogTraceDTO> response = createPagedResponse(page);

        return new LogTraceDTOPage(PageDTO.build(response));
	}
	
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

	
	/*
	 * Transforms a TraceDTO into a Map<String, Object> for the morphia queries.
	 * This is required to create a dotNotation for nested queries in mongodb.
	 */
	private static Map<String, Object> prepareQuery(TraceDTO obj) {
 	    Map<String, Object> result = new HashMap<>();
 	    BeanInfo info= null;
 		try {
 			info = Introspector.getBeanInfo(obj.getClass());
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
        assert info != null;
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
 	        Method reader = pd.getReadMethod();
 	        if (reader != null) {
                try {
                    if (!pd.getName().equals("class")) {
                        result.put("trace." + pd.getName(), reader.invoke(obj));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
 	    }

 	    if (result.get("trace.url") != null)
            result.put("trace.url", ".*" + result.get("trace.url") + ".*");

 	    return result;
 	}

 	/*
 	 * Method to transform a Page<LogTrace> into a Page<LogTraceDTO>.
 	 * The reason for this is that the id field from the mongo response
 	 * is a ObjecId, and the response to the Api should be the string
 	 * representaiton of that object.
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
