
package br.com.conductor.heimdall.core.util;

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

import org.springframework.data.domain.PageRequest;

import br.com.twsoftware.alfred.object.Objeto;
import org.springframework.data.domain.Sort;

/**
 * Class responsible for validating query paging limits.
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 * 
 */
public class Pageable extends PageRequest {

     private static final long serialVersionUID = -7059003199460696522L;

     private Pageable(Integer offset, Integer limit){
          super(offset, limit);
     }

     private Pageable(Integer offset, Integer limit, Sort sort){
          super(offset, limit, sort);
     }

     /**
      * Method responsible for setting the necessary parameters for requesting a pagination.
      * 
      * @param offset			Indicates which page should be returned.
      * 						  If no value is entered for this parameter, it will default to 0. 
      * @param limit 			Indicates the limit of records to be displayed per page.
      * 						  If no value is entered for this parameter or the value
      * 						  entered is greater than 100, it will default to 100.
      * 
      * @return {@link Pageable}
      */
     public static Pageable setPageable(Integer offset, Integer limit) {
          return setPageable(offset, limit, null);
     }

     /**
      * Method responsible for setting the necessary parameters for requesting a pagination.
      *
      * @param offset			Indicates which page should be returned.
      * 						  If no value is entered for this parameter, it will default to 0.
      * @param limit 			Indicates the limit of records to be displayed per page.
      * 						  If no value is entered for this parameter or the value
      * 						  entered is greater than 100, it will default to 100.
      * @param sort             {@link Sort}
      *
      * @return {@link Pageable}
      */
     public static Pageable setPageable(Integer offset, Integer limit, Sort sort) {
          if (offset == null) offset = 0;

          if (limit == null || limit >= 100) limit = 100;

          if (sort == null)
               return new Pageable(offset, limit);
          else
               return new Pageable(offset, limit, sort);

     }
}
