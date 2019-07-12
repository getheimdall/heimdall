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

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Environment;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.HttpMethod;
import io.swagger.models.Info;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import io.swagger.parser.Swagger20Parser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides methods to import and export Swagger.
 *
 * @author <a href="https://dijalmasilva.github.io" target="_blank">Dijalma Silva</a>
 */
@Service
public class SwaggerService {

    private final EnvironmentService environmentService;
    private final OperationService operationService;
    private final ResourceService resourceService;

    public SwaggerService(ResourceService resourceService,
                          OperationService operationService,
                          EnvironmentService environmentService) {
        this.resourceService = resourceService;
        this.operationService = operationService;
        this.environmentService = environmentService;
    }

    public Swagger exportApiToSwaggerJSON(Api api) {
        Swagger swagger = new Swagger();

        swagger.setSwagger("2.0");
        swagger.setBasePath(api.getBasePath());

        swagger.setInfo(getInfoByApi(api));
        Optional<String> optionalEnvironment = api.getEnvironments().stream().findFirst();
        optionalEnvironment.ifPresent(envId -> {
            final Environment environment = environmentService.find(envId);
            swagger.setHost(environment.getInboundURL());
        });
        swagger.setTags(getTagsByApi(api));
        swagger.setPaths(getPathsByApi(api));
        swagger.setDefinitions(new HashMap<>());
        swagger.setConsumes(new ArrayList<>());

        return swagger;
    }

    public Api importApiFromSwaggerJSON(Api api, String swaggerAsString, boolean override) throws IOException {

        List<Resource> resources;

        if (override) {
            resourceService.deleteAllFromApi(api.getId());
            resources = new ArrayList<>();
        } else {
            resources = resourceService.list(api.getId());
        }

        Swagger swagger = new Swagger20Parser().parse(swaggerAsString);

        readTags(swagger.getTags(), resources, api.getId());
        readPaths(swagger.getPaths(), api.getBasePath(), resources, api.getId());

        api.setResources(resources.stream().map(Resource::getId).collect(Collectors.toSet()));

        return api;
    }

    private void readTags(List<Tag> tags, List<Resource> resources, String apiId) {
        tags.forEach(tag -> {
            if (resourceThisTagNotExist(tag, resources)) {
                Resource resourceCreated = findResourceByTagOrCreate(tag, resources);
                resourceCreated = resourceService.save(apiId, resourceCreated);
                resources.add(resourceCreated);
            }
        });
    }

    private void readPaths(Map<String, Path> paths, String basePath, List<Resource> resources, String apiId) {

        paths.forEach(((valuePath, pathItem) -> {
            io.swagger.models.Operation get = pathItem.getGet();
            io.swagger.models.Operation put = pathItem.getPut();
            io.swagger.models.Operation post = pathItem.getPost();
            io.swagger.models.Operation patch = pathItem.getPatch();
            io.swagger.models.Operation delete = pathItem.getDelete();

            if (Objects.nonNull(get)) {
                readOperation(valuePath, basePath, get, HttpMethod.GET, resources, apiId);
            }

            if (Objects.nonNull(put)) {
                readOperation(valuePath, basePath, put, HttpMethod.PUT, resources, apiId);
            }

            if (Objects.nonNull(post)) {
                readOperation(valuePath, basePath, post, HttpMethod.POST, resources, apiId);
            }

            if (Objects.nonNull(patch)) {
                readOperation(valuePath, basePath, patch, HttpMethod.PATCH, resources, apiId);
            }

            if (Objects.nonNull(delete)) {
                readOperation(valuePath, basePath, delete, HttpMethod.DELETE, resources, apiId);
            }

        }));
    }

    private boolean resourceThisTagNotExist(Tag tag, List<Resource> resources) {
        return resources.stream().noneMatch(resource -> tag.getName().equalsIgnoreCase(resource.getName()));
    }

    private Resource findResourceByTagOrCreate(Tag tag, List<Resource> resources) {
        if (resources.isEmpty()) {
            return createResourceByTag(tag);
        }
        return resources.stream().filter(r -> r.getName().equalsIgnoreCase(tag.getName())).findFirst().orElse(createResourceByTag(tag));
    }

    private Resource createResourceByTag(Tag tag) {
        Resource resource = new Resource();
        resource.setName(tag.getName());
        resource.setDescription(tag.getDescription());
        resource.setOperations(new ArrayList<>());

        return resource;
    }

    private boolean operationNotExist(HttpMethod method, String path, List<Operation> operations) {
        return operations.stream().noneMatch(op -> op.getMethod() == method && op.getPath().equalsIgnoreCase(path));
    }

    private Operation findOperationByOperationSwaggerOrCreate(io.swagger.models.Operation operation, HttpMethod method, String path, List<Operation> operations) {
        return operations.stream().filter(op -> op.getMethod() == method && op.getPath().equalsIgnoreCase(path))
                .findFirst().orElse(createOperationByOperationSwagger(path, method, operation));
    }

    private Operation createOperationByOperationSwagger(String path, HttpMethod method, io.swagger.models.Operation operation) {
        Operation op = new Operation();
        op.setPath(path);
        op.setMethod(method);
        op.setDescription(operation.getSummary());

        return op;
    }

    private void readOperation(String valuePath, String basePath, io.swagger.models.Operation verb, HttpMethod method, List<Resource> resources, String apiId) {

        verb.getTags().forEach(tagName -> {
            Tag tag = new Tag().name(tagName);
            Resource resource = findResourceByTagOrCreate(tag, resources);
            if (resourceThisTagNotExist(tag, resources)) {
                resource = resourceService.save(apiId, resource);
                resources.add(resource);
            }

            List<Operation> operations = operationService.list(apiId, resource.getId());
            if (Objects.isNull(operations)) {
                operations = new ArrayList<>();
            }

            String path = valuePath.replace(basePath, "");
            Operation operation = findOperationByOperationSwaggerOrCreate(verb, method, path, operations);

            if (operationNotExist(method, path, operations)) {
                operation = operationService.save(apiId, resource.getId(), operation);
                operations.add(operation);
            }
        });
    }

    private Info getInfoByApi(Api api) {
        Info info = new Info();
        info.setTitle(api.getName());
        info.setDescription(api.getDescription());
        info.setVersion(api.getVersion());

        return info;
    }

    private List<Tag> getTagsByApi(Api api) {
        return api.getResources().stream()
                .map(resourceId -> {
                    final Resource resource = resourceService.find(resourceId);
                    return new Tag()
                            .name(resource.getName())
                            .description(resource.getDescription());
                })
                .collect(Collectors.toList());
    }

    private Map<String, Path> getPathsByApi(Api api) {

        Map<String, Path> pathMap = new HashMap<>();

        api.getResources().forEach(resource -> {
            final List<Operation> operations = operationService.list(api.getId(), resource);
            final Resource res = resourceService.find(resource);
            operations.forEach(operation -> {
                String pathOperation = operation.getPath();
                if (Objects.isNull(pathMap.get(pathOperation))) {
                    pathMap.put(pathOperation, new Path());
                }

                Path path = pathMap.get(pathOperation);

                io.swagger.models.Operation operationSwagger = new io.swagger.models.Operation();
                if (operation.getDescription() != null) {
                    operationSwagger.setOperationId(operation.getDescription().trim().concat(operation.getMethod().name()));
                    operationSwagger.setSummary(operation.getDescription());
                }
                operationSwagger.setConsumes(new ArrayList<>());
                operationSwagger.setTags(Collections.singletonList(res.getName()));
                operationSwagger.setDeprecated(false);
                operationSwagger.setParameters(new ArrayList<>());
                operationSwagger.setProduces(new ArrayList<>());
                operationSwagger.setResponses(new HashMap<>());

                path.set(operation.getMethod().name().toLowerCase(), operationSwagger);
            });
        });

        return pathMap;
    }
}
