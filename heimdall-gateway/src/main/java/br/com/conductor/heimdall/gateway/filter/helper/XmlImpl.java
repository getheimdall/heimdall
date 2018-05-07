
package br.com.conductor.heimdall.gateway.filter.helper;

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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.conductor.heimdall.middleware.spec.Xml;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the {@link Xml} interface.
 * 
 * @author Marcos Filho
 *
 */
@Slf4j
public class XmlImpl implements Xml {

     @Override
     public <T> String parse(T object) {

          try {
               return mapper().writeValueAsString(object);
          } catch (JsonProcessingException e) {
               log.error(e.getMessage(), e);
               return null;
          }
     }

     @SuppressWarnings("unchecked")
     @Override
     public <T> T parse(String xml, Class<?> classType) {

          try {
               return (T) mapper().readValue(xml, classType);
          } catch (IOException e) {
               log.error(e.getMessage(), e);
               return null;
          }
     }

     private ObjectMapper mapper() {

          ObjectMapper mapper = new XmlMapper();
          mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
          mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

          return mapper;
     }
}
