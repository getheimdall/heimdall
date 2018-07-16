
package br.com.conductor.heimdall.core.entity;

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

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.conductor.heimdall.core.enums.Interval;
import br.com.conductor.heimdall.core.util.LocalDateTimeDeserializer;
import br.com.conductor.heimdall.core.util.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a view of rate limit that client will define how many requests can be executed by the user. 
 * 
 * @author Marcos Filho
 *
 */
@Data
@AllArgsConstructor
public class RateLimit implements Serializable {

     public static final int MIN_REMAINING = 0;

     private static final long serialVersionUID = 7194905692521670392L;
     
     public static final String KEY = "Rate";
     private String path;
     private Long calls;
     private Interval interval;
     private Long remaining;
     @JsonDeserialize(using = LocalDateTimeDeserializer.class)
     @JsonSerialize(using = LocalDateTimeSerializer.class)
     private LocalDateTime lastRequest;
     
     public RateLimit(String path, Long calls, Interval interval) {
          this.path = path;
          this.calls = calls;
          this.interval = interval;
          reset();
     }
     
     public void decreaseRemaining() {
          if (this.remaining > MIN_REMAINING) {
               this.remaining--;
          }
     }

     public void reset() {
          this.remaining = this.calls;
          this.lastRequest = LocalDateTime.now();
     }
     
     public boolean hasRemaining() {
          return this.remaining != null && this.remaining > MIN_REMAINING;
     }
}
