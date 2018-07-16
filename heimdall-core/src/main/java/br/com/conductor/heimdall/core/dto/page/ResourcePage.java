package br.com.conductor.heimdall.core.dto.page;

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

import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.entity.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class that represents a paged {@link Resource} list.
 *
 * @author Filipe Germano
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ResourcePage extends PageDTO<Resource> implements Serializable {

     private static final long serialVersionUID = -6720212738880571229L;

     public ResourcePage(PageDTO<Resource> p){
          super(p.getNumber(), 
                    p.size, 
                    p.totalPages, 
                    p.numberOfElements, 
                    p.totalElements, 
                    p.firstPage, 
                    p.hasPreviousPage, 
                    p.hasNextPage, 
                    p.hasContent, 
                    p.first, 
                    p.last, 
                    p.nextPage, 
                    p.previousPage, 
                    p.content);          
     }

}
