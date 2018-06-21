
package br.com.conductor.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import br.com.conductor.heimdall.core.entity.LogTrace;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j
@Repository
@NoArgsConstructor
public class MongoLogConnector implements Serializable {

	 private static final long serialVersionUID = 8125889338220953042L;

     private String databaseName;

     @Autowired
     private Property property;

//     private final static Integer PAGE = 0;
//
//     private final static Integer LIMIT = 100;
//     
     @PostConstruct
     public void init() {
    	 this.databaseName = property.getMongo().getDataBase();
     }

     /**
      * Initializes the database connection by name.
      * 
      * @param databaseName
      * Database names
      */
     public MongoLogConnector(String databaseName) {

         this.databaseName = databaseName;

     }

     public LogTrace findOne(LogTrace object) {

         Object idMongo = getValueId(object);
         return this.datastore().get(object.getClass(), idMongo);
     }
     
     public List<LogTrace> findAll() {
    	 
          return datastore().createQuery(LogTrace.class).asList();
     }

//     public <T> Page<T> find(Object criteria, Integer page, Integer limit) {
//
//          Query<T> query = this.prepareQuery(criteria, this.datastore());
//
//          List<T> list = Lists.newArrayList();
//          Long totalElements = query.count();
//          
//          page = page == null ? PAGE : page;
//          limit = limit == null || limit > LIMIT ? LIMIT : limit;
//          
//          if ( page >= 1 &&  limit > 0  && limit <= LIMIT) {
//               list = query.asList(new FindOptions().limit(limit).skip(page * limit));  
//          } else {
//               list = query.asList(new FindOptions().limit(limit));
//          }
//
//          return (Page<T>) buildPage(list, page, limit, totalElements);
//     }
     
     public List<LogTrace> find(Map<String, Object> queries) {
    	 Query<LogTrace> query = this.datastore().createQuery(LogTrace.class);
    	     	 
    	 queries.forEach((k, v) -> {
    		 if (v != null) {
    			 if (k.equals("trace.url")) {
    				 query.field(k).contains((String) v);
    			 } else {
    				 query.field(k).equal(v);
    			 }
    		 }
    	 });
    	 
    	 return query.asList();
     }
     
//     public <T> Page<T> buildPage(List<T> list, Integer page, Integer limit, Long totalElements) {
//
//          Page<T> pageResponse = new Page<>();
//
//          pageResponse.number = page;
//          pageResponse.totalPages = new BigDecimal(totalElements).divide(new BigDecimal(limit), BigDecimal.ROUND_UP, 0).intValue();
//          pageResponse.numberOfElements = limit;
//          pageResponse.totalElements = totalElements;
//          pageResponse.hasPreviousPage = page > 0;
//          pageResponse.hasNextPage = page < (pageResponse.totalPages - 1);
//          pageResponse.hasContent = Objeto.notBlank(list);
//          pageResponse.first = page == 0;
//          pageResponse.last = page == (pageResponse.totalPages - 1);
//          pageResponse.nextPage = page == (pageResponse.totalPages - 1) ? page : page + 1;
//          pageResponse.previousPage = page == 0 ? 0 : page - 1;
//          pageResponse.content = list;
//
//          return pageResponse;
//     }




     private MongoClient createMongoClient() {

          MongoClient client;
          if (Objeto.notBlank(property.getMongo().getUrl())) {

               MongoClientURI uri = new MongoClientURI(property.getMongo().getUrl());
               client = new MongoClient(uri);
          } else {
               ServerAddress address = new ServerAddress(property.getMongo().getServerName(), property.getMongo().getPort().intValue());
               MongoCredential mongoCredential = MongoCredential.createCredential(property.getMongo().getUsername(), property.getMongo().getUsername(), property.getMongo().getPassword().toCharArray());
               MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
               client = new MongoClient(address, mongoCredential, mongoClientOptions);
          }

          return client;
     }

     
     private Datastore datastore() {

          Morphia morphia = new Morphia();

          return morphia.createDatastore(createMongoClient(), this.databaseName);
     }

     private <T> Object getValueId(T object) {

          Field id = Arrays.asList(object.getClass().getDeclaredFields()).stream().filter(field -> field.getAnnotation(Id.class) != null).findFirst().get();
          if (id != null) {
               id.setAccessible(true);
               try {
                    return id.get(object);
               } catch (IllegalArgumentException e) {
                    log.error(e.getMessage(), e);
               } catch (IllegalAccessException e) {
                    log.error(e.getMessage(), e);
               }
          }
          return null;
     }

}