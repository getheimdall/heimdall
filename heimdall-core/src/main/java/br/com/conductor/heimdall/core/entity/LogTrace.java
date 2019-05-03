package br.com.conductor.heimdall.core.entity;

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

import java.io.Serializable;
import java.util.Date;

import br.com.conductor.heimdall.core.trace.Trace;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Data;

/**
 * This class represents a log trace of the requests that are saved to the MongoDB
 * 
 * @author Marcelo Rodrigues
 */
@Entity(noClassnameStored=true, value="logs")
@Data
public class LogTrace implements Serializable {

	private static final long serialVersionUID = -3756531883939035456L;

	@Id
	private ObjectId id;
	
	private Trace trace;

	private String logger;
	
	private String level;
	
	private String thread;
	
	private Date ts;
	
}
