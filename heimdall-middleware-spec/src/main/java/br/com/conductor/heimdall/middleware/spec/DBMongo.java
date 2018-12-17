
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
 * This interface represents a connection to a MongoDB database .
 *
 * @author Filipe Germano
 * @author Jefferson X. Cardoso
 * @author marcos.filho
 *
 */

public interface DBMongo extends DB {

     /**
      * Inserts a List of objects to a {@link MongoCollection} of {@link Document}.
      * 
      * @param collection
      * {@link MongoCollection} of {@link Document}
      * @param object
      * List of Objects to insert
      */
     public <T> void insertMany(MongoCollection<Document> collection, List<T> objects);

     /**
      * Inserts a object in a {@link MongoCollection} of {@link Document}.
      * 
      * @param collection
      * {@link MongoCollection} of {@link Document}
      * @param object
      * Object to insert
      */
     public <T> void insertOne(MongoCollection<Document> collection, T object);

     /**
      * Returns the {@link Query} provider.
      *
      * @param criteria
      * Class type
      * @return The Query provider
      */
     public <T> Query<T> getQueryProvider(Object criteria);

     /**
      * Returns a Page from the {@link MongoCollection} of {@link Document}, classType, {@link Bson}, page number and limit.
      *
      * @param collection
      * {@link MongoCollection} of {@link Document}
      * @param classType
      * The type of paged files
      * @param filters
      * {@link Bson}
      * @param page
      * Integer that represents the page number
      * @param limit
      * Limits the number of pages to return
      * @return
      */
     public <T> Page<T> find(MongoCollection<Document> collection, Class<T> classType, Bson filters, Integer page, Integer limit);

     /**
      * Gets a {@link MongoCollection} from class type.
      * 
      * @param <T>
      * 
      * @param classType
      * The class type of the {@link MongoCollection}
      * @return The {@link MongoCollection}
      */

     public <T> MongoCollection<Document> collection(Class<T> classType);

     /**
      * Gets a {@link MongoCollection} from name.
      * 
      * @param name
      * The name of the {@link MongoCollection}
      * @return The {@link MongoCollection}
      */
     public MongoCollection<Document> collection(String name);

     /**
      * Merging object in the collection
      * 
      * @param object
      * object with id and field going to be updated
      * @return updated object
      */
     public <T> T merge(T object);

}
