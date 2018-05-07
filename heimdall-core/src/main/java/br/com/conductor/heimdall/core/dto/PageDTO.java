
package br.com.conductor.heimdall.core.dto;

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

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class is a Data Transfer Object for the {@link Page}
 *
 * @author Filipe Germano
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
     
     @JsonInclude(Include.ALWAYS)
     public int number;

     @JsonInclude(Include.ALWAYS)
     public int size;

     @JsonInclude(Include.ALWAYS)
     public int totalPages;

     @JsonInclude(Include.ALWAYS)
     public int numberOfElements;

     @JsonInclude(Include.ALWAYS)
     public long totalElements;

     @JsonInclude(Include.ALWAYS)
     public boolean firstPage;

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
     
     public static <T> PageDTO<T> build(Page<T> p) {
         if (p == null){

              return null;
         } else {
              
              PageDTO<T> page = new PageDTO<>();
              page.setContent(p.getContent());
              page.setHasContent(p.hasContent());
              page.setNumber(p.getNumber());
              page.setNumberOfElements(p.getNumberOfElements());
              page.setSize(p.getSize());
              page.setTotalElements(p.getTotalElements());
              page.setTotalPages(p.getTotalPages());
              page.setHasNextPage(p.hasNext());
              page.setHasPreviousPage(p.hasPrevious());
              page.setFirst(p.isFirst());
              page.setLast(p.isLast());
              if (p.previousPageable() != null) {
                   page.setPreviousPage(p.previousPageable().getPageNumber());
              }
              if (p.nextPageable() != null) {
                   page.setNextPage(p.nextPageable().getPageNumber());
              }
              return page;
         }
     }

}
