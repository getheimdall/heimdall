
package br.com.conductor.heimdall.core.dto.page;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
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

import java.io.Serializable;

import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.entity.Interceptor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class that represents a paged {@link Interceptor} list.
 *
 * @author Filipe Germano
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class InterceptorPage extends PageDTO<Interceptor> implements Serializable {

     private static final long serialVersionUID = -5707251207211538090L;

     public InterceptorPage(PageDTO<Interceptor> p){
          super(p.getNumber(), 
        		  p.getSize(), 
                  p.getTotalPages(), 
                  p.getNumberOfElements(), 
                  p.getTotalElements(), 
                  p.isFirstPage(), 
                  p.isHasPreviousPage(), 
                  p.isHasNextPage(), 
                  p.isHasContent(), 
                  p.isFirst(), 
                  p.isLast(), 
                  p.getNextPage(), 
                  p.getPreviousPage(), 
                  p.getContent());          
     }

}
