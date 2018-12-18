
package br.com.conductor.heimdall.gateway.trace;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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

import java.time.LocalDateTime;
import java.util.Date;

import br.com.conductor.heimdall.core.util.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * Data class that represents a General Trace
 *
 * @author Thiago Sampaio
 *
 */
@Data
public class GeneralTrace {

     private String description;

     @JsonSerialize(using = LocalDateTimeSerializer.class)
     private LocalDateTime insertedOnDate = LocalDateTime.now();

     private Object content;

     /**
      * Adds a description to the trace.
      * 
      * @param description	Trace message
      */
     public GeneralTrace(String description){

          super();
          this.description = description;
     }

     /**
      * Adds a description and Object to the trace.
      * 
      * @param description	Trace message
      * @param content		Object with content
      */
     public GeneralTrace(String description, Object content){

          super();
          this.description = description;
          this.content = content;
     }

}
