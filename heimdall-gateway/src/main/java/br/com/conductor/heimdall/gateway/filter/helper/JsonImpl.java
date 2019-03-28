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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.com.conductor.heimdall.core.dto.BeanValidationErrorDTO;
import br.com.conductor.heimdall.middleware.exception.BeanValidationException;
import br.com.conductor.heimdall.middleware.spec.Json;
import br.com.twsoftware.alfred.object.Objeto;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the {@link Json} interface.
 *
 * @author Filipe Germano
 *
 */
@Slf4j
public class JsonImpl implements Json {

	public String parse(Map<String, Object> body) {

		try {
			String json = mapper().writeValueAsString(body);

			return json;
		} catch (JsonProcessingException e) {

			log.error(e.getMessage(), e);
			return null;
		}
	}

	public String parse(String string) {

		try {

			JSONObject jsonObject = new JSONObject(string);

			return jsonObject.toString();
		} catch (JSONException e) {
			try {
				JSONArray array = new JSONArray(string);
				return array.toString();
			} catch (JSONException ex1) {
				if (Objeto.notBlank(string)) {
					return string;
				}
			}
		}

		return null;
	}

	public <T> String parse(T object) {

		try {

			String jsonInString = mapper().writeValueAsString(object);

			return jsonInString;
		} catch (Exception e) {

			log.error(e.getMessage(), e);
			return null;
		}
	}

	public <T> T parse(String json, Class<?> classType) throws BeanValidationException {

		try {
			@SuppressWarnings("unchecked")
			T obj = (T) mapper().readValue(json, classType);

			List<BeanValidationErrorDTO> errors = beanValidation(obj);

			if (!errors.isEmpty()) {

				String jsonViolations = parse(errors);

				throw new BeanValidationException("Bean validation error.", jsonViolations);
			}

			return obj;
		} catch (BeanValidationException e) {
			log.debug(e.getMessage(), e);
			throw e;
		} catch (Exception e) {

			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public <T> T parse(String json, TypeReference<T> type) throws BeanValidationException {
		try {
			mapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			@SuppressWarnings("unchecked")
			T obj = (T) mapper().readValue(json, type);

			List<BeanValidationErrorDTO> errors = beanValidation(obj);

			if (!errors.isEmpty()) {

				String jsonViolations = parse(errors);

				throw new BeanValidationException("Bean validation error.", jsonViolations);
			}

			return obj;
		} catch (BeanValidationException e) {
			log.debug(e.getMessage(), e);
			throw e;
		} catch (Exception e) {

			log.error(e.getMessage(), e);
			return null;
		}
	}


	public <T> T parse(String json, Class<?> parametrized, Class<?>... parameterClasses) throws BeanValidationException {
		try {
			@SuppressWarnings("unchecked")
			T obj = (T) mapper().readValue(json, TypeFactory.defaultInstance().constructParametricType(parametrized, parameterClasses));

			List<BeanValidationErrorDTO> errors = beanValidation(obj);
			
			if (!errors.isEmpty()) {

				String jsonViolations = parse(errors);

				throw new BeanValidationException("Bean validation error.", jsonViolations);
			}

			return obj;
		} catch (BeanValidationException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	public <T> Map<String, Object> parseToMap(T object) {

		try {
			ObjectMapper mapper = mapper().setSerializationInclusion(Include.NON_NULL);
			String jsonInString = mapper.writeValueAsString(object);

			@SuppressWarnings("unchecked")
			Map<String, Object> map = mapper.readValue(jsonInString, Map.class);
			return map;
		} catch (Exception e) {

			log.error(e.getMessage(), e);
			return null;
		}
	}

	public boolean isJson(String string) {

		try {
			JsonParser parser = new ObjectMapper().getFactory().createParser(string);

			while (parser.nextToken() != null) {}

			return true;
		} catch (IOException e) {
		    return false;
        }

	}

	private ObjectMapper mapper() {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

		return mapper;
	}
	
	private <T> List<BeanValidationErrorDTO> beanValidation(T bean) {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
        	Validator validator = factory.getValidator();
        	
        	Set<ConstraintViolation<T>> violations = validator.validate(bean);
        	
        	return violations.stream().map(v -> {
				return new BeanValidationErrorDTO(v.getPropertyPath().toString(), v.getMessage());
			}).collect(Collectors.toList());
        }
   }
}
