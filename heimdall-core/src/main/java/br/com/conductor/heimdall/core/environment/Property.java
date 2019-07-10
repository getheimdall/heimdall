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
package br.com.conductor.heimdall.core.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * This class represents the environment.
 * 
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Data
@ConfigurationProperties(prefix = "heimdall")
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

     private Trace trace = new Trace();

     @Data
     public class Trace {
          private boolean printAllTrace = false;
          private List<String> sanitizes = new ArrayList<>();
          private boolean printHeimdallFilters = false;
     }

     private FailSafe failsafe = new FailSafe();
     
     @Data
     public class FailSafe {
        private boolean enabled = true;
    	private int failureNumber = 3;
    	private int successNumber = 3;
    	private int delayTimeSeconds = 30;
     }

     @Data
     public class Interceptor {
        private Health health = new Health();
     }

     @Data
     public class Health {
          private Long fixedRate;
     }

}
