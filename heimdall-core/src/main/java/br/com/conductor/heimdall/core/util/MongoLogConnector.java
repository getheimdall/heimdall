
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

import br.com.conductor.heimdall.core.dto.logs.FiltersDTO;
import br.com.conductor.heimdall.core.enums.Periods;
import br.com.conductor.heimdall.core.dto.metrics.Metric;
import br.com.conductor.heimdall.core.entity.LogTrace;
import br.com.conductor.heimdall.core.environment.Property;
import br.com.twsoftware.alfred.object.Objeto;
import com.mongodb.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Group;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class creates a connection fo the MongoDB used by Heimdall
 * to save its logs.
 * 
 * @author Marcelo Aguiar Rodrigues
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

     private final static Integer PAGE = 0;

     private final static Integer LIMIT = 100;

     private MongoClient client;
     
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

    /**
     * Finds one specific trace.
     *
     * @param object LogTraceDTO
     * @return Trace found
     */
     public LogTrace findOne(LogTrace object) {

         Object idMongo = getValueId(object);
         return this.datastore().get(object.getClass(), idMongo);
     }

    /**
     * Creates a paged result of the filters informed.
     *
     * @param filtersDTOS Filters for the search
     * @param page Page wanted
     * @param limit Number of records per page
     * @return Paged list of traces
     */
     public Page<LogTrace> find(List<FiltersDTO> filtersDTOS, Integer page, Integer  limit) {
         Query<LogTrace> query = this.prepareQuery(filtersDTOS);

         return preparePage(query, page, limit);
     }

    /**
     * Creates a descending list of metrics for a specified period of time.
     *
     * @param id Trace field wanted
     * @param size max number of elements to return
     * @param period period of time wanted
     * @return List of metrics
     */
     public List<Metric> findByTop(String id, int size, Periods period) {
         final Datastore datastore = this.datastore();
         Query<LogTrace> query = prepareRange(datastore.createQuery(LogTrace.class), period);

         query.field(id).notEqual(null);

         final AggregationPipeline pipeline = datastore.createAggregation(LogTrace.class)
                 .match(query)
                 .group(id,
                         Group.grouping("metric", Group.last(id)),
                         Group.grouping("value", Accumulator.accumulator("$sum", 1)))
                 .sort(Sort.descending("value"))
                 .limit(size);

         final Iterator<Metric> aggregate = pipeline.aggregate(Metric.class);

         List<Metric> list = new ArrayList<>();
         aggregate.forEachRemaining(list::add);

         return list;
     }

     public List<Metric> findByMetricBySum(String id, String source, String metric, Periods period) {
         final Datastore datastore = this.datastore();
         Query<LogTrace> query = prepareRange(datastore.createQuery(LogTrace.class), period);

         query.field(source).equal(id);

         final AggregationPipeline aggregation = datastore.createAggregation(LogTrace.class)
                 .match(query)
                 .group(metric,
                         Group.grouping("metric", Group.last(metric)),
                         Group.grouping("value", Accumulator.accumulator("$sum", 1)));

         final Iterator<Metric> aggregate = aggregation.aggregate(Metric.class);

         List<Metric> list = new ArrayList<>();
         aggregate.forEachRemaining(list::add);

         return list;
     }

     public List<Metric> findByMetricByAvg(String id, String source, String metric, Periods period) {

         final Datastore datastore = this.datastore();
         Query<LogTrace> query = prepareRange(datastore.createQuery(LogTrace.class), period);

         query.field(source).equal(id);

         final AggregationPipeline aggregation = datastore.createAggregation(LogTrace.class)
                 .match(query)
                 .group(source,
                         Group.grouping("metric", Group.last(source)),
                         Group.grouping("value", Accumulator.accumulator("$avg", metric)));

         final Iterator<Metric> aggregate = aggregation.aggregate(Metric.class);

         List<Metric> list = new ArrayList<>();
         aggregate.forEachRemaining(list::add);

         return list;
     }

     private Query<LogTrace> prepareRange(Query<LogTrace> query, Periods date) {
         String insertedOnDate = "trace.insertedOnDate";
         switch(date) {
             case TODAY: {
                 query.field(insertedOnDate).containsIgnoreCase(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
             }
             case YESTERDAY: {
                 query.field(insertedOnDate).containsIgnoreCase(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));
             }
             case THIS_WEEK: {
                 Map<String, LocalDate> week = CalendarUtils.firstAndLastDaysOfWeek(LocalDate.now());
                 query.field(insertedOnDate).greaterThanOrEq(week.get("first").format(DateTimeFormatter.ISO_DATE));
                 query.field(insertedOnDate).lessThanOrEq(week.get("last").format(DateTimeFormatter.ISO_DATE));
                 break;
             }
             case LAST_WEEK: {
                 Map<String, LocalDate> week = CalendarUtils.firstAndLastDaysOfWeek(LocalDate.now().minusWeeks(1));
                 query.field(insertedOnDate).greaterThanOrEq(week.get("first").format(DateTimeFormatter.ISO_DATE));
                 query.field(insertedOnDate).lessThanOrEq(week.get("last").format(DateTimeFormatter.ISO_DATE));
                 break;
             }
             case THIS_MONTH: {
                 query.field(insertedOnDate).containsIgnoreCase(CalendarUtils.yearAndMonth(LocalDate.now()));
                 break;
             }
             case LAST_MONTH: {
                 query.field(insertedOnDate).containsIgnoreCase(CalendarUtils.yearAndMonth(LocalDate.now().minusMonths(1)));
                 break;
             }
         }
         return query;
     }

     private Query<LogTrace> prepareQuery(List<FiltersDTO> filtersDTOs) {
         Query<LogTrace> query = this.datastore().createQuery(LogTrace.class);

         filtersDTOs.forEach(filtersDTO -> {

             Object value1, value2;

             try {
                 value1 = Integer.parseInt(filtersDTO.getFirstValue());
             } catch (NumberFormatException e) {
                 value1 = filtersDTO.getFirstValue();
             }

             try {
                 value2 = Integer.parseInt(filtersDTO.getSecondValue());
             }catch (NumberFormatException e) {
                 value2 = filtersDTO.getSecondValue();
             }

             switch (filtersDTO.getOperationSelected()) {
                 case EQUALS: {
                     query.field(filtersDTO.getName()).equal(value1);
                     break;
                 }
                 case NOT_EQUALS: {
                     query.field(filtersDTO.getName()).notEqual(value1);
                     break;
                 }
                 case CONTAINS: {
                     query.field(filtersDTO.getName()).containsIgnoreCase(value1.toString());
                     break;
                 }
                 case BETWEEN: {
                     query.field(filtersDTO.getName()).greaterThanOrEq(value1);
                     query.field(filtersDTO.getName()).lessThanOrEq(value2);
                     break;
                 }
                 case LESS_THAN: {
                     query.field(filtersDTO.getName()).lessThan(value1);
                     break;
                 }
                 case LESS_THAN_EQUALS: {
                     query.field(filtersDTO.getName()).lessThanOrEq(value1);
                     break;
                 }
                 case GREATER_THAN: {
                     query.field(filtersDTO.getName()).greaterThan(value1);
                     break;
                 }
                 case GREATER_THAN_EQUALS: {
                     query.field(filtersDTO.getName()).greaterThanOrEq(value1);
                     break;
                 }
                 case ALL: {
                     query.field(filtersDTO.getName()).exists();
                     break;
                 }
                 case NONE: {
                     query.field(filtersDTO.getName()).doesNotExist();
                     break;
                 }
                 case TODAY: {
                     query.field(filtersDTO.getName())
                             .containsIgnoreCase(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
                     break;
                 }
                 case YESTERDAY: {
                     query.field(filtersDTO.getName())
                             .containsIgnoreCase(LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE));
                     break;
                 }
                 case THIS_WEEK: {
                     Map<String, LocalDate> week = CalendarUtils.firstAndLastDaysOfWeek(LocalDate.now());
                     query.field(filtersDTO.getName()).greaterThanOrEq(week.get("first").format(DateTimeFormatter.ISO_DATE));
                     query.field(filtersDTO.getName()).lessThanOrEq(week.get("last").format(DateTimeFormatter.ISO_DATE));
                     break;
                 }
                 case LAST_WEEK: {
                     Map<String, LocalDate> week = CalendarUtils.firstAndLastDaysOfWeek(LocalDate.now().minusWeeks(1));
                     query.field(filtersDTO.getName()).greaterThanOrEq(week.get("first").format(DateTimeFormatter.ISO_DATE));
                     query.field(filtersDTO.getName()).lessThanOrEq(week.get("last").format(DateTimeFormatter.ISO_DATE));
                     break;
                 }
                 case THIS_MONTH: {
                     query.field(filtersDTO.getName()).containsIgnoreCase(CalendarUtils.yearAndMonth(LocalDate.now()));
                     break;
                 }
                 case LAST_MONTH: {
                     query.field(filtersDTO.getName()).containsIgnoreCase(CalendarUtils.yearAndMonth(LocalDate.now().minusMonths(1)));
                     break;
                 }
                 case THIS_YEAR: {
                     query.field(filtersDTO.getName()).containsIgnoreCase(CalendarUtils.year(LocalDate.now()));
                     break;
                 }
             }
         });

         return query;
     }

     private Page<LogTrace> preparePage(Query<LogTrace> query, Integer page, Integer limit) {
         List<LogTrace> list;
         Long totalElements = query.count();

         query = query.order("-ts");

         page = page == null ? PAGE : page;
         limit = limit == null || limit > LIMIT ? LIMIT : limit;

         if (page >= 1 && limit > 0) {
             list = query.asList(new FindOptions().limit(limit).skip(page * limit));
         } else {
             list = query.asList(new FindOptions().limit(limit));
         }

         return buildPage(list, page, limit, totalElements);
     }

     private Page<LogTrace> buildPage(List<LogTrace> list, Integer page, Integer limit, Long totalElements) {

          Page<LogTrace> pageResponse = new Page<>();

          pageResponse.number = page;
          pageResponse.totalPages = new BigDecimal(totalElements).divide(new BigDecimal(limit), BigDecimal.ROUND_UP, 0).intValue();
          pageResponse.numberOfElements = limit;
          pageResponse.totalElements = totalElements;
          pageResponse.hasPreviousPage = page > 0;
          pageResponse.hasNextPage = page < (pageResponse.totalPages - 1);
          pageResponse.hasContent = Objeto.notBlank(list);
          pageResponse.first = page == 0;
          pageResponse.last = page == (pageResponse.totalPages - 1);
          pageResponse.nextPage = page == (pageResponse.totalPages - 1) ? page : page + 1;
          pageResponse.previousPage = page == 0 ? 0 : page - 1;
          pageResponse.content = list;

          return pageResponse;
     }

     private void createMongoClient() {

          if (Objeto.notBlank(property.getMongo().getUrl())) {

               MongoClientURI uri = new MongoClientURI(property.getMongo().getUrl());
               this.client = new MongoClient(uri);
          } else {
               ServerAddress address = new ServerAddress(property.getMongo().getServerName(), property.getMongo().getPort().intValue());
               MongoCredential mongoCredential = MongoCredential.createCredential(property.getMongo().getUsername(), property.getMongo().getUsername(), property.getMongo().getPassword().toCharArray());
               MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();
               this.client = new MongoClient(address, mongoCredential, mongoClientOptions);
          }
     }

     private Datastore datastore() {

          Morphia morphia = new Morphia();

          if (this.client == null) {
              this.createMongoClient();
          }

          return morphia.createDatastore(this.client, this.databaseName);
     }

     private <T> Object getValueId(T object) {

          Field id = Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.getAnnotation(Id.class) != null).findFirst().orElse(null);
          if (id != null) {
               id.setAccessible(true);
               try {
                    return id.get(object);
               } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error(e.getMessage(), e);
               }
          }
          return null;
     }

}