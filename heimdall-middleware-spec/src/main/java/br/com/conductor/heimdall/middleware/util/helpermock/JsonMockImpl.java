
package br.com.conductor.heimdall.middleware.util.helpermock;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
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

import br.com.conductor.heimdall.middleware.spec.Json;
import br.com.twsoftware.alfred.object.Objeto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Mock class created to help unit test the root request class of a middleware.
 *
 * @author Marcelo Aguiar
 */
public class JsonMockImpl implements Json {

    public String parse(Map<String, Object> body) {

        try {
            return mapper().writeValueAsString(body);
        } catch (JsonProcessingException e) {

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
                if (Objeto.notBlank(string))
                    return string;
            }
        }

        return null;
    }

    public <T> String parse(T object) {

        try {

            return mapper().writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T parse(String json, Class<?> classType) {

        try {
            mapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            @SuppressWarnings("unchecked")
            T obj = (T) mapper().readValue(json, classType);
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    public <T> Map<String, Object> parseToMap(T object) {

        try {
            mapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ObjectMapper mapper = mapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String jsonInString = mapper.writeValueAsString(object);

            @SuppressWarnings("unchecked")
            Map<String, Object> map = mapper.readValue(jsonInString, Map.class);
            return map;
        } catch (Exception e) {

            return null;
        }
    }

    public boolean isJson(String string) {

        boolean valid = false;
        try {
            JSONObject jsonObject = new JSONObject(string);

            if (Objeto.notBlank(jsonObject))
                valid = true;
        } catch (JSONException e) {

            try {
                new JSONArray(string);
                valid = true;
            } catch (JSONException ignored) { }
        }

        return valid;
    }

    private ObjectMapper mapper() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper;
    }
}
