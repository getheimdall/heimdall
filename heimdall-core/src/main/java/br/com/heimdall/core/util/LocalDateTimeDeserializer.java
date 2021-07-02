
package br.com.heimdall.core.util;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 *
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * This class provides a method to deserialize a <i>json</i> to a data and time format.
 * 
 * @author Marcos Filho
 *
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

     private LocalDateTimeDeserializer(){}

     private static final DateTimeFormatter ISO_DATE_TIME;

     static {
          ISO_DATE_TIME = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_DATE_TIME).toFormatter();
     }

     @Override
     public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {

          switch (parser.getCurrentToken()) {
               case START_ARRAY:
                    if (parser.nextToken() == JsonToken.END_ARRAY) {
                         return null;
                    }
                    int year = parser.getIntValue();

                    parser.nextToken();
                    int month = parser.getIntValue();

                    parser.nextToken();
                    int day = parser.getIntValue();

                    parser.nextToken();
                    int hour = parser.getIntValue();

                    parser.nextToken();
                    int minute = parser.getIntValue();

                    parser.nextToken();
                    int second = parser.getIntValue();

                    parser.nextToken();
                    int nanosecond = parser.getIntValue();

                    if (parser.nextToken() != JsonToken.END_ARRAY) {
                         context.reportWrongTokenException(JsonToken.class, JsonToken.END_ARRAY, "Expected array to end.");
                    }
                    return LocalDateTime.of(year, month, day, hour, minute, second, nanosecond);

               case VALUE_STRING:
                    String string = parser.getText().trim();
                    if (string.length() == 0) {
                         return null;
                    }
                    return LocalDateTime.parse(string, ISO_DATE_TIME);
               default:
                    break;
          }
          context.reportWrongTokenException(JsonToken.class, JsonToken.START_ARRAY, "Expected array or string.");
          return null;
     }
}
