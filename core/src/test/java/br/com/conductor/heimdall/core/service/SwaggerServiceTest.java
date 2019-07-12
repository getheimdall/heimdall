///*-
// * =========================LICENSE_START==================================
// * heimdall-core
// * ========================================================================
// * Copyright (C) 2018 Conductor Tecnologia SA
// * ========================================================================
// * Licensed under the Apache License, Version 2.0 (the "License")
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * ==========================LICENSE_END===================================
// */
//
//package br.com.conductor.heimdall.core.service;
//
//import br.com.conductor.heimdall.core.dto.ResourceDTO;
//import br.com.conductor.heimdall.core.entity.*;
//import br.com.conductor.heimdall.core.enums.HttpMethod;
//import br.com.conductor.heimdall.core.enums.Status;
//import io.swagger.models.Path;
//import io.swagger.models.Swagger;
//import io.swagger.models.Tag;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.io.IOException;
//import java.util.*;
//
//import static org.junit.Assert.*;
//
///**
// * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
// **/
//@RunWith(MockitoJUnitRunner.class)
//public class SwaggerServiceTest {
//
//    @InjectMocks
//    private SwaggerService swaggerService;
//
//    @Mock
//    private ResourceService resourceService;
//
//    @Mock
//    private OperationService operationService;
//
//    private Api api;
//
//    @Before
//    public void before() {
//        api = new Api();
//        api.setId(1L);
//        api.setCors(false);
//        api.setBasePath("/api");
//        api.setDescription("Api Description");
//        api.setName("Api Name");
//        api.setVersion("1.0.0");
//        api.setStatus(Status.ACTIVE);
//
//        List<Environment> environments = new ArrayList<>();
//        Set<Resource> resources = new HashSet<>();
//        List<Plan> plans = new ArrayList<>();
//        List<Operation> operations = new ArrayList<>();
//
//        Resource resource = new Resource();
//        resource.setId(1L);
//        resource.setName("resource");
//        resource.setDescription("resource description");
//        resource.setApi(api);
//
//        Operation operation = new Operation();
//        operation.setId(1L);
//        operation.setResource(resource);
//        operation.setMethod(HttpMethod.GET);
//        operation.setDescription("operation description");
//        operation.setPath("/operation");
//
//        operations.add(operation);
//        resource.setOperations(operations);
//
//        Plan plan = new Plan();
//        plan.setId(1L);
//        plan.setApi(api);
//        plan.setDefaultPlan(true);
//        plan.setName("plan");
//        plan.setScopes(null);
//        plan.setStatus(Status.ACTIVE);
//
//        Environment environment = new Environment();
//        environment.setId("1");
//        environment.setInboundURL("http://localhost:8080");
//        environment.setOutboundURL("http://localhost:8080");
//        environment.setName("environment");
//        environment.setDescription("environment description");
//        environment.setVariables(null);
//        environment.setStatus(Status.ACTIVE);
//
//        environments.add(environment);
//        resources.add(resource);
//        plans.add(plan);
//
//        api.setEnvironments(environments);
//        api.setResources(resources);
//        api.setPlans(plans);
//    }
//
//    @Test
//    public void exportApiToSwaggerJSON() {
//        Set<Resource> resources = api.getResources();
//        Resource resource = resources.iterator().next();
//        String path = resource.getOperations().get(0).getPath();
//
//        Swagger swagger = swaggerService.exportApiToSwaggerJSON(api);
//
//        String basePath = swagger.getBasePath();
//        String host = swagger.getHost();
//        List<Tag> tags = swagger.getTags();
//        Tag tag = tags.get(0);
//        String tagName = tag.getName();
//        Map<String, Path> paths = swagger.getPaths();
//        Path pathSwagger = paths.get(path);
//
//        assertNotNull(pathSwagger);
//        io.swagger.models.Operation get = pathSwagger.getGet();
//
//        assertEquals(api.getBasePath(), basePath);
//        assertEquals(api.getEnvironments().get(0).getInboundURL(), host);
//        assertEquals(tagName, resource.getName());
//        assertTrue(get.getTags().contains(tagName));
//    }
//
//    @Test
//    public void importApiFromSwaggerJSONWithoutOverride() throws IOException {
//        importApiFromSwaggerJSON(false);
//    }
//
//
//    @Test
//    public void importApiFromSwaggerJSONWithOverride() throws IOException {
//        importApiFromSwaggerJSON(true);
//    }
//
//    private void importApiFromSwaggerJSON(boolean override) throws IOException {
//        String swaggerAsString = "{\n" +
//                "    \"swagger\": \"2.0\",\n" +
//                "    \"info\": {\n" +
//                "        \"description\": \"Api Description\",\n" +
//                "        \"version\": \"1.0.0\",\n" +
//                "        \"title\": \"Api\"\n" +
//                "    },\n" +
//                "    \"host\": \"http://localhost:8080\",\n" +
//                "    \"basePath\": \"/api\",\n" +
//                "    \"tags\": [{\n" +
//                "        \"name\": \"resource\",\n" +
//                "        \"description\": \"resource description\"\n" +
//                "    }],\n" +
//                "    \"paths\": {\n" +
//                "        \"/operation\": {\n" +
//                "            \"get\": {\n" +
//                "                \"tags\": [\n" +
//                "                    \"resource\"\n" +
//                "                ],\n" +
//                "                \"summary\": \"operation description\",\n" +
//                "                \"operationId\": \"operation descriptionGET\",\n" +
//                "                \"deprecated\": false\n" +
//                "            }\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";
//
//
//        String inboundURLExpected = api.getEnvironments().get(0).getInboundURL();
//        Resource resourceExpected = api.getResources().iterator().next();
//        Operation operationExpected = resourceExpected.getOperations().get(0);
//
//        Mockito.when(resourceService.list(Mockito.anyLong(), Mockito.any(ResourceDTO.class))).thenReturn(new ArrayList<>(api.getResources()));
//        Mockito.when(resourceService.save(Mockito.anyLong(), Mockito.any(Resource.class))).thenReturn(resourceExpected);
//        Mockito.when(resourceService.find(Mockito.anyLong(), Mockito.anyLong())).thenReturn(resourceExpected);
//        Mockito.when(operationService.save(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(Operation.class))).thenReturn(operationExpected);
//        Mockito.when(operationService.find(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(operationExpected);
//
//        Api apiResult = swaggerService.importApiFromSwaggerJSON(this.api, swaggerAsString, override);
//        String inboundURLActual = apiResult.getEnvironments().get(0).getInboundURL();
//        Resource resourceActual = apiResult.getResources().iterator().next();
//        Operation operationActual = resourceActual.getOperations().get(0);
//
//        assertEquals(api.getName(), apiResult.getName());
//        assertEquals(api.getVersion(), apiResult.getVersion());
//        assertEquals(api.getDescription(), apiResult.getDescription());
//        assertEquals(api.getBasePath(), apiResult.getBasePath());
//        assertEquals(inboundURLExpected, inboundURLActual);
//        assertEquals(resourceExpected.getName(), resourceActual.getName());
//        assertEquals(operationExpected.getPath(), operationActual.getPath());
//        assertEquals(operationExpected.getMethod(), operationActual.getMethod());
//    }
//}
