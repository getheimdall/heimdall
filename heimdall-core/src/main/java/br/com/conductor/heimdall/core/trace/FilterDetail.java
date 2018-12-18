
package br.com.conductor.heimdall.core.trace;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data class that represents the Filter details.
 *
 * @author Thiago Sampaio
 *
 */
@Data
public class FilterDetail {

     private String name;

     private long timeInMillisRun;

     private long timeInMillisShould;

     private String status;

     @Setter(value=AccessLevel.NONE)
     @Getter(AccessLevel.NONE)
     private long totalTimeInMillis;

     @JsonInclude(JsonInclude.Include.NON_NULL)
     private StackTrace stackTrace;

     /**
      * Returns the total time in milliseconds.
      * 
      * @return Time in milliseconds
      */
     public long getTotalTimeInMillis() {

          return timeInMillisRun + timeInMillisShould;
     }
}
