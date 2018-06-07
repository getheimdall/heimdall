
package br.com.conductor.heimdall.middleware.spec;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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
import br.com.conductor.heimdall.middleware.util.Page;

/**
 * This interface represents a connection to a database.
 *
 * @author Filipe Germano
 * @author Jefferson X. Cardoso
 *
 */
public interface DB {

     /**
      * Save a record in DB.
      * 
      * @param object
      * Object to be saved in DB
      * 
      */
     public <T> T save(T object);

     /**
      * Update a record in the DB.
      * 
      * @param object
      * Object to be updated in DB
      * 
      */
     public <T> T update(T object);

     /**
      * Delete a record in DB
      * 
      * @param object
      * Object to be deleted in DB
      */
     public <T> Boolean delete(T object);

     /**
      * Find a object in DB
      * 
      * @param object
      * @return
      */
     public <T> T findOne(T object);

     /**
      * Find all objects in DB
      * 
      * @return
      */
     public <T> List<T> findAll(Class<T> classType);

     /**
      * Find a documents
      *
      * @param object
      */
     public <T> Page<T> find(Object criteria, Integer page, Integer limit);

     /**
      * Builds a Page from a List of Objects, a page number to start, a limit of pages to create and a number of total elements.
      *
      * @param list
      * The List of Objects
      * @param page
      * The start page
      * @param limit
      * The limit of pages to create
      * @param totalElements
      * The total number of elements
      * @return The {@link Page} list of elements
      */
     public <T> Page<T> buildPage(List<T> list, Integer page, Integer limit, Long totalElements);

}
