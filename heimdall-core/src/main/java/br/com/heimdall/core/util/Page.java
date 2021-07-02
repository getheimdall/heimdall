
package br.com.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * This class provides a paging system for a response from a database.
 *
 * @author Filipe Germano
 *
 */
@Data
public class Page<T> {
     
     @JsonInclude(Include.ALWAYS)
     private int number;

     @JsonInclude(Include.ALWAYS)
     private int totalPages;

     @JsonInclude(Include.ALWAYS)
     private int numberOfElements;

     @JsonInclude(Include.ALWAYS)
     private long totalElements;

     @JsonInclude(Include.ALWAYS)
     private boolean hasPreviousPage;

     @JsonInclude(Include.ALWAYS)
     private boolean hasNextPage;

     @JsonInclude(Include.ALWAYS)
     private boolean hasContent;

     @JsonInclude(Include.ALWAYS)
     private boolean first;

     @JsonInclude(Include.ALWAYS)
     private boolean last;

     @JsonInclude(Include.ALWAYS)
     private int nextPage;

     @JsonInclude(Include.ALWAYS)
     private int previousPage;

     private List<T> content;

}
