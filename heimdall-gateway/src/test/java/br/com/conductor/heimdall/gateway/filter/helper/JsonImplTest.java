package br.com.conductor.heimdall.gateway.filter.helper;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import br.com.conductor.heimdall.core.dto.BeanValidationErrorDTO;
import br.com.conductor.heimdall.middleware.exception.BeanValidationException;
import lombok.Data;

@RunWith(MockitoJUnitRunner.class)
public class JsonImplTest {

     @InjectMocks
     private JsonImpl subject;
     
     @Test(expected = BeanValidationException.class)
     public void notBlankAnnotationShouldWork() {
          String json = "{\"name\":\"\"}";
          
          try {
               subject.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = subject.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("attribute", is("name"))));
               throw e;
          }
     }
     
     @Test(expected = BeanValidationException.class)
     public void minAnnotationShouldWork() {
          String json = "{\"name\":\"Getu\", \"age\": 9}";
          
          try {
               subject.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = subject.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("message", is("You have to be older than 18 to drive in Brasil"))));
               throw e;
          }
     }
     
     @Test(expected = BeanValidationException.class)
     public void interpolationWithMessageExpressionsShouldWork() {
          String json = "{\"name\":\"Getu\", \"age\": 20, \"socialMedia\": 0}";
          
          try {
               subject.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = subject.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("message", is("You have to have at least 2 social medias"))));
               throw e;
          }
     }
     
     @Test(expected = BeanValidationException.class)
     public void pastAnnotationShouldWork() {
          LocalDate tomorrow = LocalDate.now().plusDays(1);
          
          String json = String.format("{\"name\":\"Getu\", \"age\": 20, \"socialMedia\": 3, \"birth\": \"%s\"}", tomorrow.toString());
          
          try {
               subject.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = subject.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("attribute", is("birth"))));
               throw e;
          }
     }
     
     @Test(expected = BeanValidationException.class)
     public void emailAnnotationShouldWork() {
          LocalDate yesterday = LocalDate.now().minusDays(1);
          
          String json = String.format("{\"name\":\"Getu\", \"age\": 20, \"socialMedia\": 3, \"birth\": \"%s\", \"email\": \"heimdall_at_heimdall.com\"}", yesterday.toString());
          
          try {
               subject.parse(json , PersonJsonTest.class);
          } catch (BeanValidationException e) {
               List<BeanValidationErrorDTO> errors = subject.parse(e.getViolations(), new TypeReference<List<BeanValidationErrorDTO>>() {});
               assertThat(errors, contains(hasProperty("attribute", is("email"))));
               throw e;
          }
     }
     
     @Test
     public void noBeanValidationShouldRaise() {
          LocalDate yesterday = LocalDate.now().minusDays(1);
          
          String json = String.format("{\"name\":\"Getu\", \"age\": 20, \"socialMedia\": 3, \"birth\": \"%s\", \"email\": \"heimdall@heimdall.com\"}", yesterday.toString());
          
          PersonJsonTest person = subject.parse(json , PersonJsonTest.class);
          
          assertThat(person, hasProperty("name", is("Getu")));
          assertThat(person, hasProperty("age", is(20)));
          assertThat(person, hasProperty("socialMedia", is(3)));
          assertThat(person, hasProperty("birth", is(yesterday)));
          assertThat(person, hasProperty("email", is("heimdall@heimdall.com")));
     }
     
     @Data
     public static class PersonJsonTest {
          @NotBlank
          private String name;
          
          @Min(value = 18, message = "You have to be older than {value} to drive in Brasil")
          private Integer age;
          
          @Min(value = 2, message = "You have to have at least {value} social media${value > 1 ? 's' : ''}")
          private Integer socialMedia;
          
          @Past
          @JsonDeserialize(using = LocalDateDeserializer.class)
          private LocalDate birth;
          
          @Email
          private String email;
     }
}