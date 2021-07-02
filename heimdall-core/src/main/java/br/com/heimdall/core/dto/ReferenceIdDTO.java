
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class is a Data Transfer Object for the ReferenceId.
 *
 * @author Filipe Germano
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceIdDTO implements Serializable {

     private static final long serialVersionUID = -7325639885315556027L;
     
     private Long id;

}
