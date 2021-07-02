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
package br.com.heimdall.core.dto.page;

import br.com.heimdall.core.dto.PageDTO;
import br.com.heimdall.core.entity.Scope;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Class that represents a paged {@link Scope} list.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ScopePage extends PageDTO<Scope> implements Serializable {

    private static final long serialVersionUID = -6720212738880571229L;

    public ScopePage(PageDTO<Scope> p){
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
