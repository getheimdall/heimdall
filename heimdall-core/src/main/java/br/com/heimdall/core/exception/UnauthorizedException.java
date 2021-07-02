
package br.com.heimdall.core.exception;

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

/**
 * This class represents the exceptions related with a resource request
 * that was invalidated due to business logic.
 * 
 * @author Filipe Germano
 * @see HeimdallException
 *
 */
public class UnauthorizedException extends HeimdallException {

     private static final long serialVersionUID = -6083332085555491000L;

     public UnauthorizedException(ExceptionMessage exeptionMessage){
          
          super(exeptionMessage);
     }

}
