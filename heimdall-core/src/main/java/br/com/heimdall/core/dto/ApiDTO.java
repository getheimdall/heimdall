
package br.com.heimdall.core.dto;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
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
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.heimdall.core.entity.Api;
import br.com.heimdall.core.enums.Status;
import lombok.Data;

/**
 * Class is a Data Transfer Object for the {@link Api}.
 *
 * @author Filipe Germano
 *
 */
@Data
public class ApiDTO implements Serializable {

     private static final long serialVersionUID = 8788858214468281712L;

     @NotNull
     @Size(max = 80)
     private String name;

     @NotNull
     @Size(max = 40)
     private String version;

     @Size(max = 200)
     private String description;

     @NotNull
     @Size(max = 80)
     private String basePath;

     @Size(max = 200)
     private String destinationProduction;
     
     @Size(max = 200)
     private String destinationSandbox;
  
     private List<String> tags;
     
     private Status status;
     
     private List<ReferenceIdDTO> environments;

}
