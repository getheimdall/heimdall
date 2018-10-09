
package br.com.conductor.heimdall.api.configuration;

/*-
 * =========================LICENSE_START==================================
 * heimdall-api
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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import br.com.conductor.heimdall.core.environment.Property;
import br.com.twsoftware.alfred.object.Objeto;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the {@link EnvironmentAware} interface.
 *
 * @author Filipe Germano
 *
 */
@Configuration
@Slf4j
public class DataBaseConfiguration implements EnvironmentAware {

     @Autowired
     Property property;

     private final String SQL_SERVER = "SQL_SERVER";

     @Value("${spring.jpa.database}")
     private String database;

     private RelaxedPropertyResolver liquiBasePropertyResolver;

     private SpringLiquibase liquibase;

     @Override
     public void setEnvironment(Environment environment) {

          this.liquiBasePropertyResolver = new RelaxedPropertyResolver(environment, "liquiBase.");
     }
     
     /**
      * Returns a {@link DataSource} configured acording to a {@link Property}.
      * 
      * @return {@link DataSource}
      */
     @Bean
     @Profile("!test")
     public DataSource dataSource() {

          DataSource dataSource;

          HikariConfig hikariConfig = new HikariConfig();

          hikariConfig.setInitializationFailTimeout(property.getDatasource().getInitializationFailTimeout());
          hikariConfig.setMinimumIdle(property.getDatasource().getMinimumIdle());
          hikariConfig.setMaximumPoolSize(property.getDatasource().getMaximumPoolSize());
          hikariConfig.setValidationTimeout(property.getDatasource().getValidationTimeout());
          hikariConfig.setIdleTimeout(property.getDatasource().getIdleTimeout());
          hikariConfig.setConnectionTimeout(property.getDatasource().getConnectionTimeout());
          hikariConfig.setAutoCommit(property.getDatasource().getAutoCommit());
          hikariConfig.setDataSourceClassName(property.getDatasource().getDataSourceClassName());
          hikariConfig.setConnectionTestQuery(property.getDatasource().getConnectionTestQuery());
          hikariConfig.addDataSourceProperty("databaseName", property.getDatasource().getDatabaseName());
          hikariConfig.addDataSourceProperty("serverName", property.getDatasource().getServerName());                              
          hikariConfig.addDataSourceProperty("user", property.getDatasource().getUsername());
          hikariConfig.addDataSourceProperty("password", property.getDatasource().getPassword());
          hikariConfig.addDataSourceProperty("socketTimeout", property.getDatasource().getSocketTimeout());
          hikariConfig.addDataSourceProperty("loginTimeout", property.getDatasource().getLoginTimeout());
          hikariConfig.addDataSourceProperty("applicationName", property.getDatasource().getAppName());
          hikariConfig.addDataSourceProperty("portNumber", property.getDatasource().getPortNumber());
          
          if (SQL_SERVER.equals(database)) {
               
               hikariConfig.addDataSourceProperty("sendStringParametersAsUnicode", property.getDatasource().isSendStringParametersAsUnicode());
               hikariConfig.addDataSourceProperty("multiSubnetFailover", property.getDatasource().isMultiSubnetFailover());
          }

          if (Objeto.notBlank(property.getDatasource().getEncrypt())) {
               
               hikariConfig.addDataSourceProperty("encrypt", property.getDatasource().getEncrypt());
          }
          
          if (Objeto.notBlank(property.getDatasource().getTrustServerCertificate())) {
               
               hikariConfig.addDataSourceProperty("trustServerCertificate", property.getDatasource().getTrustServerCertificate());
          }
          
          if (Objeto.notBlank(property.getDatasource().getHostNameInCertificate())) {
               
               hikariConfig.addDataSourceProperty("hostNameInCertificate", property.getDatasource().getHostNameInCertificate());
          }
          
          dataSource = new HikariDataSource(hikariConfig);

          return dataSource;
     }
     
     /**
      * Configures a {@link SpringLiquibase} based on a {@link DataSource}.
      * 
      * @param dataSource		{@link DataSource}
      * @return					{@link SpringLiquibase}
      */
     @Bean
     public SpringLiquibase liquibase(DataSource dataSource) {

          liquibase = new SpringLiquibase();
          liquibase.setDataSource(dataSource);
          liquibase.setChangeLog("classpath:liquibase/master.xml");
          liquibase.setContexts(liquiBasePropertyResolver.getProperty("context"));
          liquibase.setShouldRun(property.getDatasource().isRunLiquibase());

          releaseLiquibaseLocks(dataSource);
          clearLiquibaseCheckSums(dataSource);

          log.debug("Configuring Liquibase and versioning the database ... Please wait!");

          return liquibase;
     }

     /**
      * Release all Liquibase locks from a {@link DataSource}.
      * 
      * @param ds			{@link DataSource}
      */
     public void releaseLiquibaseLocks(DataSource ds) {

          try {

               log.info("Releasing Liquibase Locks");

               @Cleanup
               Connection con = ds.getConnection();

               @Cleanup
               Statement st = con.createStatement();
               st.executeUpdate("DELETE FROM DATABASECHANGELOGLOCK");

               con.commit();

          } catch (SQLException e) {
               log.info("Error while trying to delete DATABASECHANGELOGLOCK");
          }
     }

     /**
      * Clears all Liquibase checksums from a {@link DataSource}.
      * 
      * @param ds			{@link DataSource}
      */
     public void clearLiquibaseCheckSums(DataSource ds) {

          try {

               log.info("Clear Liquibase ChecksSums");

               @Cleanup
               Connection con = ds.getConnection();

               @Cleanup
               Statement st = con.createStatement();
               st.executeUpdate("UPDATE DATABASECHANGELOG SET MD5SUM=NULL");

               con.commit();

          } catch (SQLException e) {
               log.info("Error while trying to delete MD5SUM");
          }
     }

}
