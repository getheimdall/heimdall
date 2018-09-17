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
package br.com.conductor.heimdall.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

/**
 * This class provides a paging system for a response from a database.
 *
 * @author Filipe Germano
 *
 */
@Data
public class Page<T> {
     
     @JsonInclude(Include.ALWAYS)
     public int number;

     @JsonInclude(Include.ALWAYS)
     public int totalPages;

     @JsonInclude(Include.ALWAYS)
     public int numberOfElements;

     @JsonInclude(Include.ALWAYS)
     public long totalElements;

     @JsonInclude(Include.ALWAYS)
     public boolean hasPreviousPage;

     @JsonInclude(Include.ALWAYS)
     public boolean hasNextPage;

     @JsonInclude(Include.ALWAYS)
     public boolean hasContent;

     @JsonInclude(Include.ALWAYS)
     public boolean first;

     @JsonInclude(Include.ALWAYS)
     public boolean last;

     @JsonInclude(Include.ALWAYS)
     public int nextPage;

     @JsonInclude(Include.ALWAYS)
     public int previousPage;

     public List<T> content;

}
