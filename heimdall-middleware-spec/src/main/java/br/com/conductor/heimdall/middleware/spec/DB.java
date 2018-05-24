
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

import org.bson.Document;
import org.bson.conversions.Bson;
import org.mongodb.morphia.query.Query;

import com.mongodb.client.MongoCollection;

import br.com.conductor.heimdall.middleware.util.Page;

/**
 * This interface represents a connection to a mongoDB database.
 *
 * @author Filipe Germano
 *
 */
public interface DB {
     
     /**
      * Inserts a List of objects to a {@link MongoCollection} of {@link Document}.
      * 
      * @param collection     {@link MongoCollection} of {@link Document}
      * @param object         List of Objects to insert
      */
	public <T> void insertMany(MongoCollection<Document> collection, List<T> objects); 
     
	/**
      * Inserts a object in a {@link MongoCollection} of {@link Document}.
      * 
      * @param collection     {@link MongoCollection} of {@link Document}
      * @param object         Object to insert
      */
     public <T> void insertOne(MongoCollection<Document> collection, T object);
     
     /**
      * Builds a Page from a List of Objects, a page number to start, a limit of pages to create and a number of total elements.
      * 
      * @param list                	The List of Objects
      * @param page                	The start page
      * @param limit               	The limit of pages to create
      * @param totalElements		The total number of elements
      * @return                     The {@link Page} list of elements
      */
     public <T> Page<T> buildPage(List<T> list, Integer page, Integer limit, Long totalElements);
     
     /**
      * Returns a Page from the {@link MongoCollection} of {@link Document}, classType, {@link Bson}, page number and limit.
      * 
      * @param collection     	{@link MongoCollection} of {@link Document}
      * @param classType 		The type of paged files
      * @param filters        	{@link Bson}
      * @param page           	Integer that represents the page number
      * @param limit          	Limits the number of pages to return
      * @return
      */
     public <T> Page<T> find(MongoCollection<Document> collection, Class<T> classType, Bson filters, Integer page, Integer limit);
     
     /**
      * Delete a document of the collection
      * 
      * @param object   
      */
     public void delete(Object object);
     
     /**
      * Save a document of the collection.
      * 
      * @param object   Object to be saved to the DB
      */
     public <T> T save(T object);
     
     /**
      * Returns the {@link Query} provider.
      * 
      * @param object
      * @return				The Query provider
      */
     public <T> Query<T> getQueryProvider(Object criteria);
     
     /**
      * Find a documents
      * 
      * @param object   
      */
     public <T> Page<T> find(Object criteria, Integer page, Integer limit);
     
     /**
      * Gets a {@link MongoCollection} from class type.
      * @param <T>
       * 
       * @param classType     The class type of the {@link MongoCollection}
      * @return               The {@link MongoCollection}
      */

     public <T> MongoCollection<Document> collection(Class<T> classType);
     
     /**
      * Gets a {@link MongoCollection} from name.
      * 
      * @param name               The name of the {@link MongoCollection}
      * @return                   The {@link MongoCollection}
      */
     public MongoCollection<Document> collection(String name);
}
