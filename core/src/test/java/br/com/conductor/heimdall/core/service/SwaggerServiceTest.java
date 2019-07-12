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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.entity.*;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import br.com.conductor.heimdall.core.enums.Status;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;
import org.testng.collections.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 **/
@RunWith(MockitoJUnitRunner.class)
public class SwaggerServiceTest {

    @InjectMocks
    private SwaggerService swaggerService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private OperationService operationService;

    @Mock
    private EnvironmentService environmentService;

    private Api api;
    private Resource resource = new Resource();
    private Operation operation = new Operation();
    private Environment environment = new Environment();

    @Before
    public void before() {
        api = new Api();
        api.setId("1L");
        api.setCors(false);
        api.setBasePath("/api");
        api.setDescription("Api Description");
        api.setName("Api Name");
        api.setVersion("1.0.0");
        api.setStatus(Status.ACTIVE);


        resource.setId("1L");
        resource.setName("resource");
        resource.setDescription("resource description");
        resource.setApiId(api.getId());

        operation.setId("1L");
        operation.setResourceId(resource.getId());
        operation.setMethod(HttpMethod.GET);
        operation.setDescription("operation description");
        operation.setPath("/operation");

        List<String> operations = Lists.newArrayList(operation.getId());
        resource.setOperations(operations);

        Plan plan = new Plan();
        plan.setId("1L");
        plan.setApiId(api.getId());
        plan.setDefaultPlan(true);
        plan.setName("plan");
        plan.setScopes(null);
        plan.setStatus(Status.ACTIVE);

        environment.setId("1");
        environment.setInboundURL("http://localhost:8080");
        environment.setOutboundURL("http://localhost:8080");
        environment.setName("environment");
        environment.setDescription("environment description");
        environment.setVariables(null);
        environment.setStatus(Status.ACTIVE);

        Set<String> environments = Sets.newSet(environment.getId());
        Set<String> resources = Sets.newSet(resource.getId());
        Set<String> plans = Sets.newSet(plan.getId());

        api.setEnvironments(environments);
        api.setResources(resources);
        api.setPlans(plans);

        Mockito.when(environmentService.find(Mockito.anyString())).thenReturn(environment);
        Mockito.when(resourceService.find(Mockito.anyString())).thenReturn(resource);
        Mockito.when(operationService.list(Mockito.anyString(), Mockito.anyString())).thenReturn(Lists.newArrayList(operation));

    }

    @Test
    public void exportApiToSwaggerJSON() {
        String path = operation.getPath();

        Swagger swagger = swaggerService.exportApiToSwaggerJSON(api);

        String basePath = swagger.getBasePath();
        String host = swagger.getHost();
        List<Tag> tags = swagger.getTags();
        Tag tag = tags.get(0);
        String tagName = tag.getName();
        Map<String, Path> paths = swagger.getPaths();
        Path pathSwagger = paths.get(path);

        assertNotNull(pathSwagger);
        io.swagger.models.Operation get = pathSwagger.getGet();

        assertEquals(api.getBasePath(), basePath);
        assertEquals(environment.getInboundURL(), host);
        assertEquals(tagName, resource.getName());
        assertTrue(get.getTags().contains(tagName));
    }

    @Test
    public void importApiFromSwaggerJSONWithoutOverride() throws IOException {
        importApiFromSwaggerJSON(false);
    }


    @Test
    public void importApiFromSwaggerJSONWithOverride() throws IOException {
        importApiFromSwaggerJSON(true);
    }

    private void importApiFromSwaggerJSON(boolean override) throws IOException {
        String swaggerAsString = "{\n" +
                "    \"swagger\": \"2.0\",\n" +
                "    \"info\": {\n" +
                "        \"description\": \"Api Description\",\n" +
                "        \"version\": \"1.0.0\",\n" +
                "        \"title\": \"Api\"\n" +
                "    },\n" +
                "    \"host\": \"http://localhost:8080\",\n" +
                "    \"basePath\": \"/api\",\n" +
                "    \"tags\": [{\n" +
                "        \"name\": \"resource\",\n" +
                "        \"description\": \"resource description\"\n" +
                "    }],\n" +
                "    \"paths\": {\n" +
                "        \"/operation\": {\n" +
                "            \"get\": {\n" +
                "                \"tags\": [\n" +
                "                    \"resource\"\n" +
                "                ],\n" +
                "                \"summary\": \"operation description\",\n" +
                "                \"operationId\": \"operation descriptionGET\",\n" +
                "                \"deprecated\": false\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";


        String inboundURLExpected = environment.getInboundURL();

        Mockito.when(resourceService.list(Mockito.anyString())).thenReturn(Lists.newArrayList(resource));
        Mockito.when(resourceService.save(Mockito.anyString(), Mockito.any(Resource.class))).thenReturn(resource);

        Api apiResult = swaggerService.importApiFromSwaggerJSON(this.api, swaggerAsString, override);
        String inboundURLActual = apiResult.getEnvironments().contains(environment.getId()) ? this.environment.getInboundURL() : "";
        Resource resourceActual = apiResult.getResources().contains(resource.getId()) ? resource : new Resource();
        Operation operationActual = resourceActual.getOperations().contains(operation.getId()) ? operation : new Operation();

        assertEquals(api.getName(), apiResult.getName());
        assertEquals(api.getVersion(), apiResult.getVersion());
        assertEquals(api.getDescription(), apiResult.getDescription());
        assertEquals(api.getBasePath(), apiResult.getBasePath());
        assertEquals(inboundURLExpected, inboundURLActual);
        assertEquals(resource.getName(), resourceActual.getName());
        assertEquals(operation.getPath(), operationActual.getPath());
        assertEquals(operation.getMethod(), operationActual.getMethod());
    }
}
