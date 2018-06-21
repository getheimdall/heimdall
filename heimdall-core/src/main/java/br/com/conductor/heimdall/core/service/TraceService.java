
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conductor.heimdall.core.dto.TraceDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.entity.LogTrace;
import br.com.conductor.heimdall.core.util.MongoLogConnector;
import br.com.twsoftware.alfred.object.Objeto;

/**
 * This class provides methods to read the (@link LogTrace} resource.
 * 
 * @author Marcelo Aguiar
 */
@Service
public class TraceService {
	
	@Autowired
	private MongoLogConnector mongoConnection;
	
	public List<LogTrace> findAll() {
		
		return mongoConnection.findAll();
	}
	
	
	public List<LogTrace> listWithPageableAndFilter(TraceDTO traceDTO, PageableDTO pageableDTO) {
		
		Map<String, Object> query = prepareQuery(traceDTO);
		
		if (Objeto.notBlank(query.get("trace.url"))) {
			query.put("trace.url", ".*" + query.get("trace.url") + ".*");
		}
			
		List<LogTrace> page = mongoConnection.find(query);

		return page;
	}
	
	public LogTrace findById(String id) {
		ObjectId oid = new ObjectId(id);
		LogTrace object = new LogTrace();
		object.setId(oid);
		
		return mongoConnection.findOne(object);
	}

	private static Map<String, Object> prepareQuery(Object obj) {
 	    Map<String, Object> result = new HashMap<String, Object>();
 	    BeanInfo info= null;
 		try {
 			info = Introspector.getBeanInfo(obj.getClass());
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 	    for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
 	        Method reader = pd.getReadMethod();
 	        if (reader != null)
 				try {
 					if (!pd.getName().equals("class"))
 						result.put("trace." + pd.getName(), reader.invoke(obj));
 				} catch (Exception e) {
 					e.printStackTrace();
 				
 				}
 	    }
 	    return result;
 	}
}
