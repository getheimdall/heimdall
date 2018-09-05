package br.com.conductor.heimdall.gateway.filter.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.util.List;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.type.TypeReference;

import br.com.conductor.heimdall.core.dto.BeanValidationErrorDTO;
import br.com.conductor.heimdall.middleware.exception.BeanValidationException;
import lombok.Data;

@RunWith(MockitoJUnitRunner.class)
public class JsonImplTest {

     @InjectMocks
     private JsonImpl sujeito;
     
     @Test(expected = BeanValidationException.class)
     public void validarAnotacaoNotBlank() {
          String json = "{\"name\":\"\"}";
          
          try {
               sujeito.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = sujeito.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("attribute", is("name"))));
               throw e;
          }
     }
     
     @Test(expected = BeanValidationException.class)
     public void validarAnotacaoMin() {
          String json = "{\"name\":\"Getu\", \"age\": 9}";
          
          try {
               sujeito.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = sujeito.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("message", is("You have to be older than 18 to drive in Brasil"))));
               throw e;
          }
     }
     
     @Data
     static class PersonJsonTest {
          @NotBlank
          private String name;
          
          @Min(value = 18, message = "You have to be older than {value} to drive in Brasil")
          private Integer age;
     }
}