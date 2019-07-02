/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
package br.com.conductor.heimdall.api.configuration;

import br.com.conductor.heimdall.core.environment.Property;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

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

    @Value("${spring.jpa.database}")
     private String database;

     @Override
     public void setEnvironment(Environment environment) {

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

         String SQL_SERVER = "SQL_SERVER";
         if (SQL_SERVER.equals(database)) {
               
               hikariConfig.addDataSourceProperty("sendStringParametersAsUnicode", property.getDatasource().isSendStringParametersAsUnicode());
               hikariConfig.addDataSourceProperty("multiSubnetFailover", property.getDatasource().isMultiSubnetFailover());
          }

          if (property.getDatasource().getEncrypt() != null) {
               
               hikariConfig.addDataSourceProperty("encrypt", property.getDatasource().getEncrypt());
          }
          
          if (property.getDatasource().getTrustServerCertificate() != null) {
               
               hikariConfig.addDataSourceProperty("trustServerCertificate", property.getDatasource().getTrustServerCertificate());
          }
          
          if (property.getDatasource().getHostNameInCertificate() != null) {
               
               hikariConfig.addDataSourceProperty("hostNameInCertificate", property.getDatasource().getHostNameInCertificate());
          }
          
          dataSource = new HikariDataSource(hikariConfig);

          return dataSource;
     }


}
