
package br.com.conductor.heimdall.gateway.filter.helper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.conductor.heimdall.middleware.spec.Xml;
import lombok.extern.slf4j.Slf4j;

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
