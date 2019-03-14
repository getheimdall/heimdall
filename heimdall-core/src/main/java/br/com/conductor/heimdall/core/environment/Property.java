
package br.com.conductor.heimdall.core.environment;

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

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * This class represents the environment.
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 *
 */
@Data
@ConfigurationProperties(prefix = "heimdall", ignoreUnknownFields = true)
public class Property {

     private String contextPath;

     private Logstash logstash = new Logstash();

     @Data
     public class Logstash {

          private Boolean enabled;

          private String destination;

     }
     
     private Rewrite rewrite = new Rewrite();

     @Data
     public class Rewrite{

          private Boolean enable;

          private String prefix;

     }

     private Datasource datasource = new Datasource();

     @Data
     public class Datasource{

          private Long initializationFailTimeout;

          private String dataBaseType;

          private Integer socketTimeout;

          private String loginTimeout;

          private Integer minimumIdle;

          private Integer maximumPoolSize;

          private Long validationTimeout;

          private Long idleTimeout;

          private Long connectionTimeout;

          private Boolean autoCommit;

          private String dataSourceClassName;

          private String connectionTestQuery;

          private String appName;

          private String databaseName;

          private String portNumber;

          private String serverName;

          private String username;

          private String password;
          
          private Boolean encrypt;
          
          private Boolean trustServerCertificate;
          
          private String hostNameInCertificate;          

          private boolean runLiquibase;

          private boolean sendStringParametersAsUnicode;

          private boolean multiSubnetFailover;
          
          private String url;

     }
     
     private Redis redis = new Redis();
     
     @Data
     public class Redis {
          private String host;
          private Integer port;
          private Integer maxTotal;
          private Integer maxIdle;
          private Integer minIdle;
          private boolean testOnBorrow;
          private boolean testOnReturn;
          private boolean testWhileIdle;
          private Long minEvictableIdleTimeSeconds;
          private Long timeBetweenEvictionRunsSeconds;
          private Integer numTestsPerEvictionRun;
          private boolean blockWhenExhausted;
          private Integer connectionPoolSize;
     }
     
     private Mongo mongo = new Mongo();

     @Data
     public class Mongo {

          private Boolean enabled;
          private String serverName;
          private String url;
          private Long port;
          private String dataBase;
          private String collection;
          private String username;
          private String password;
          private Long queueSize;
          private Long discardingThreshold;
          private String zoneId;
          
     }

     private Trace trace = new Trace();

     @Data
     public class Trace {
          private boolean printAllTrace = false;
          private List<String> sanitizes = new ArrayList<>();
     }
     
     private Middlewares middlewares = new Middlewares();
     
     @Data
     public class Middlewares {
    	 private Integer allowInactive;
    	 private Boolean deleteDeprecated;
     }
     
     private FailSafe failsafe = new FailSafe();
     
     @Data
     public class FailSafe {
    	private int failureNumber = 3;
    	private int sucessNumber = 3;
    	private int delayTimeSeconds = 30;
     }

}
