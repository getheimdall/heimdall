/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.conductor.heimdall.gateway.filter.helper;

import br.com.conductor.heimdall.middleware.spec.DB;
import br.com.conductor.heimdall.middleware.spec.DBMongo;
import br.com.conductor.heimdall.middleware.spec.Json;
import br.com.conductor.heimdall.middleware.util.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implements the {@link DB} interface.
 * 
 * @author Filipe Germano
 * @author Magdiel Matias
 * @author Daniel Vilar
 * @author Jefferson X. Cardoso
 *
 */
@Slf4j
public class DBMongoImpl implements DBMongo {

     private Json json = new JsonImpl();

     private String databaseName;

     private final static Integer PAGE = 0;

     private final static Integer LIMIT = 100;

	 private MongoClient mongoClient;

     /**
      * Initializes the database connection by name.
      * 
      * @param databaseName
      * Database names
      */
     public DBMongoImpl(String databaseName, MongoClient mongoClient) {

          this.databaseName = databaseName;
          this.mongoClient = mongoClient;
     }

     @Override
     public <T> T save(T object) {

          try {
               this.datastore().save(object);
               return object;
          } catch (Exception e) {
               log.error(e.getMessage(), e);
               return null;
          }
     }

     @Override
     public <T> T update(T object) {

          return save(object);
     }

     @Override
     public <T> Boolean delete(T object) {

          this.datastore().delete(object);
          return true;
     }

     @Override
     public <T> T findOne(T object) {

          Object idMongo = getValueId(object);
          return (T) this.datastore().get(object.getClass(), idMongo);
     }

     @Override
     public <T> List<T> findAll(Class<T> classType) {

          return datastore().createQuery(classType).asList();
     }

     @Override
     public <T> Page<T> find(Object criteria, Integer page, Integer limit) {

          Query<T> query = this.prepareQuery(criteria, this.datastore());

          List<T> list;
          Long totalElements = query.count();
          
          page = page == null ? PAGE : page;
          limit = limit == null || limit > LIMIT ? LIMIT : limit;
          
          if (page >= 1 && limit > 0) {
               list = query.asList(new FindOptions().limit(limit).skip(page * limit));  
          } else {
               list = query.asList(new FindOptions().limit(limit));
          }

          return buildPage(list, page, limit, totalElements);
     }

     @Override
     public <T> Page<T> buildPage(List<T> list, Integer page, Integer limit, Long totalElements) {

          Page<T> pageResponse = new Page<>();

          pageResponse.number = page;
          pageResponse.totalPages = new BigDecimal(totalElements).divide(new BigDecimal(limit), BigDecimal.ROUND_UP, 0).intValue();
          pageResponse.numberOfElements = limit;
          pageResponse.totalElements = totalElements;
          pageResponse.hasPreviousPage = page > 0;
          pageResponse.hasNextPage = page < (pageResponse.totalPages - 1);
          pageResponse.hasContent = list != null && list.size() > 0;
          pageResponse.first = page == 0;
          pageResponse.last = page == (pageResponse.totalPages - 1);
          pageResponse.nextPage = page == (pageResponse.totalPages - 1) ? page : page + 1;
          pageResponse.previousPage = page == 0 ? 0 : page - 1;
          pageResponse.content = list;

          return pageResponse;
     }

	@Override
	public <T> void insertMany(MongoCollection<Document> collection, List<T> objects) {

		try {

			List<Document> ts = new ArrayList<>();
			for (T t : objects) {

				ts.add(Document.parse(json.parse(t)));
			}
			collection.insertMany(ts);
			this.datastore().save(ts);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public <T> void insertOne(MongoCollection<Document> collection, T object) {

		try {

			Document document = Document.parse(json.parse(object));
			collection.insertOne(document);
		} catch (Exception e) {

			log.error(e.getMessage(), e);
		}
	}

     @Override
     public <T> Query<T> getQueryProvider(Object criteria) {

          return this.prepareQuery(criteria, this.datastore());
     }

     @Override
     public <T> Page<T> find(MongoCollection<Document> collection, Class<T> classType, Bson filters, Integer page, Integer limit) {

          page = page == null ? PAGE : page;
          limit = limit == null || limit > LIMIT ? LIMIT : limit;
          FindIterable<Document> documents;

          if (page > 0 && limit > 0) {

               if (filters != null) {

                    documents = collection.find(Filters.and(filters)).limit(limit).skip(page * limit);
               } else {

                    documents = collection.find().limit(limit).skip(page * limit);
               }
          } else if (page == 0 && limit > 0) {

               if (filters == null) {

                    documents = collection.find().limit(limit);
               } else {

                    documents = collection.find(Filters.and(filters)).limit(limit);
               }
          } else if (limit > 0) {

               if (filters == null) {

                    documents = collection.find().limit(limit);
               } else {

                    documents = collection.find(Filters.and(filters)).limit(limit);
               }
          } else {

               if (filters != null) {

                    documents = collection.find(Filters.and(filters)).limit(LIMIT);
               } else {

                    documents = collection.find().limit(LIMIT);
               }
          }

          Long totalElements = collection.count();

          List<T> list = new ArrayList<>();
          for (Document document : documents) {
            T parse;
			try {
				parse = new ObjectMapper().readValue(document.toJson(), classType);
			} catch (IOException e) {
				log.error("Json Parser error", e);
				parse = null;
			}
               list.add(parse);
          }

          return buildPage(list, page, limit, totalElements);
     }

     @Override
     public MongoCollection<Document> collection(String name) {

          return database().getCollection(name);
     }

     @Override
     public <T> MongoCollection<Document> collection(Class<T> classType) {

          return database().getCollection(classType.getSimpleName());
     }

     @Override
     public <T> T merge(T object) {

          try {
               Datastore ds = this.datastore();
               ds.merge(object);
               return findOne(object);
          } catch (Exception e) {
               log.error(e.getMessage(), e);
               return null;
          }

     }

     private Datastore datastore() {
    	 
          return new Morphia().createDatastore(this.mongoClient, this.databaseName);
     }

     private MongoDatabase database() {

          return this.mongoClient.getDatabase(databaseName);
     }

     private <T> Object getValueId(T object) {

          Field id = Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null).findFirst().get();
          id.setAccessible(true);
          try {
               return id.get(object);
          } catch (IllegalArgumentException | IllegalAccessException e) {
               log.error(e.getMessage(), e);
          }
          return null;
     }

     private <T> Query<T> prepareQuery(Object criteria, Datastore dataStore) {

          Query<T> query = (Query<T>) dataStore.createQuery(criteria.getClass());

          List<Field> fields = this.getAllModelFields(criteria.getClass());
          for (Field field : fields) {
               field.setAccessible(true);
               Object value = null;
               try {
                    value = field.get(criteria);
               } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error(e.getMessage(), e);
               }
               if (value != null) {
                    query.criteria(field.getName()).equal(value);
               }
          }

          return query;
     }

     private <T> List<Field> getAllModelFields(Class<T> aClass) {

          List<Field> results = new ArrayList<>();

          List<Field> fields = Arrays.asList(aClass.getDeclaredFields());
          fields.forEach(field -> {

               if (!"serialVersionUID".equals(field.getName()))
                    results.add(field);
          });

          return results;
     }

}