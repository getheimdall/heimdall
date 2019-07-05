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

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import br.com.conductor.heimdall.core.util.ConstantsCache;
import br.com.conductor.heimdall.core.util.Pageable;
import br.com.conductor.heimdall.core.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * This class provides methods to create, read, update and delete a {@link Operation} resource.
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class OperationService {

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private InterceptorService interceptorService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private AMQPRouteService amqpRoute;

    @Autowired
    private AMQPCacheService amqpCacheService;

    /**
     * Finds a {@link Operation} by its Id, {@link Resource} Id and {@link Api} Id.
     *
     * @param apiId       The {@link Api} Id
     * @param resourceId  The {@link Resource} Id
     * @param operationId The {@link Operation} Id
     * @return The {@link Operation} found
     */
    @Transactional(readOnly = true)
    public Operation find(String apiId, String resourceId, String operationId) {

        resourceService.find(apiId, resourceId);

        return this.find(operationId);
    }

    public Operation find(String id) {

        Operation operation = operationRepository.findById(id).orElse(null);
        HeimdallException.checkThrow(operation == null, GLOBAL_NOT_FOUND, "Operation");

        return operation;
    }

    /**
     * Generates a paged list of {@link Operation} from a request.
     *
     * @param apiId      The {@link Api} Id
     * @param resourceId The {@link Resource} Id
     * @param pageable   The {@link Pageable}
     * @return The paged {@link Operation} list
     */
    @Transactional(readOnly = true)
    public Page<Operation> list(String apiId, String resourceId, Pageable pageable) {

        final List<Operation> operations = this.list(apiId, resourceId);

        return new PageImpl<>(operations, pageable, operations.size());
    }

    /**
     * Generates a list of {@link Operation} from a request.
     *
     * @param apiId      The {@link Api} Id
     * @param resourceId The {@link Resource} Id
     * @return The list of {@link Operation}
     */
    @Transactional(readOnly = true)
    public List<Operation> list(String apiId, String resourceId) {

        resourceService.find(apiId, resourceId);

        final List<Operation> operations = operationRepository.findAll();

        return operations.stream()
                .filter(operation -> resourceId.equals(operation.getResourceId()))
                .collect(Collectors.toList());
    }

    /**
     * Lists all {@link Operation} from one {@link Api}
     *
     * @param apiId The {@link Api} Id
     * @return The complete list of all {@link Operation} from the {@link Api}
     */
    @Transactional(readOnly = true)
    public List<Operation> list(final String apiId) {
        apiService.find(apiId);

        final List<Operation> allOperations = operationRepository.findAll();

        final List<Operation> operations = allOperations.stream()
                .filter(operation -> {
                    final Resource resource = resourceService.find(apiId, operation.getResourceId());
                    return apiId.equals(resource.getApiId());
                })
                .collect(Collectors.toList());

        if (!operations.isEmpty()) {
            for (Operation operation : operations) {
                operation.setDescription(null);
                operation.setResourceId(null);
            }
        }

        return operations;
    }

    /**
     * Saves a {@link Operation} to the repository.
     *
     * @param apiId      The {@link Api} Id
     * @param resourceId The {@link Resource} Id
     * @return The saved {@link Operation}
     */
    @Transactional
    public Operation save(String apiId, String resourceId, final Operation operation) {

        final Resource resource = resourceService.find(apiId, resourceId);
        final Api api = apiService.find(apiId);

        operation.setPath(StringUtils.removeMultipleSlashes(operation.getPath()));
        operation.fixBasePath();

        HeimdallException.checkThrow(
                this.list(apiId).stream()
                        .anyMatch(op -> op.getPath().equals(operation.getPath()) && op.getMethod().equals(operation.getMethod())),
                ONLY_ONE_OPERATION_PER_RESOURCE);

        HeimdallException.checkThrow(validatePath(api.getBasePath() + "/" + operation.getPath()), OPERATION_ROUTE_ALREADY_EXISTS);

        operation.setResourceId(resourceId);
        operation.setApiId(apiId);

        HeimdallException.checkThrow(validateSingleWildCardOperationPath(operation), OPERATION_CANT_HAVE_SINGLE_WILDCARD);
        HeimdallException.checkThrow(validateDoubleWildCardOperationPath(operation), OPERATION_CANT_HAVE_DOUBLE_WILDCARD_NOT_AT_THE_END);

        final Operation savedOperation = operationRepository.save(operation);

        resource.addOperation(savedOperation.getId());

        resourceService.update(apiId, resourceId, resource);

        amqpRoute.dispatchRoutes();

        return savedOperation;
    }

    private boolean validatePath(String pattern) {
        final List<Api> apis = apiService.list();
        final Set<String> routes = new HashSet<>();

        apis.forEach(api -> this.list(api.getId())
                .forEach(operation -> routes.add(api.getBasePath() + operation.getPath())));

        return routes.contains(pattern);
    }

    /**
     * TODO
     * Updates a {@link Operation} by its Id, {@link Api} Id, {@link Resource} Id
     *
     * @param apiId            The {@link Api} Id
     * @param resourceId       The {@link Resource} Id
     * @param operationId      The {@link Operation} Id
     * @param operationPersist The {@link Operation}
     * @return The updated {@link Operation}
     */
    @Transactional
    public Operation update(String apiId, String resourceId, String operationId, Operation operationPersist) {

        Operation operation = this.find(apiId, resourceId, operationId);
        final Api api = apiService.find(apiId);

        Operation resData = operationRepository.findByResourceApiIdAndMethodAndPath(apiId, operationPersist.getMethod(), operationPersist.getPath());
        HeimdallException.checkThrow(resData != null &&
                resData.getResourceId().equals(operation.getResourceId()) &&
                !resData.getId().equals(operation.getId()), ONLY_ONE_OPERATION_PER_RESOURCE);

        operation = GenericConverter.mapper(operationPersist, operation);
        operation.setPath(StringUtils.removeMultipleSlashes(operation.getPath()));
        operation.fixBasePath();

        HeimdallException.checkThrow(validatePath(api.getBasePath() + "/" + operation.getPath()), OPERATION_ROUTE_ALREADY_EXISTS);

        HeimdallException.checkThrow(validateSingleWildCardOperationPath(operation), OPERATION_CANT_HAVE_SINGLE_WILDCARD);
        HeimdallException.checkThrow(validateDoubleWildCardOperationPath(operation), OPERATION_CANT_HAVE_DOUBLE_WILDCARD_NOT_AT_THE_END);

        operation = operationRepository.save(operation);

        amqpRoute.dispatchRoutes();

        amqpCacheService.dispatchClean(ConstantsCache.OPERATION_ACTIVE_FROM_ENDPOINT, api.getBasePath() + operation.getPath());

        return operation;
    }

    /**
     * Deletes a {@link Operation} by its Id, {@link Resource} Id and {@link Api} Id.
     *
     * @param apiId       The {@link Api} Id
     * @param resourceId  The {@link Resource} Id
     * @param operationId The {@link Operation} Id
     */
    @Transactional
    public void delete(String apiId, String resourceId, String operationId) {

        Operation operation = this.find(apiId, resourceId, operationId);

        final Api api = apiService.find(apiId);

        // Deletes all interceptors attached to the Operation
        interceptorService.deleteAllfromOperation(operationId);

        resourceService.removeOperation(operation);

        operationRepository.delete(operation);

        amqpCacheService.dispatchClean(ConstantsCache.OPERATION_ACTIVE_FROM_ENDPOINT, api.getBasePath() + operation.getPath());

        amqpRoute.dispatchRoutes();
    }

    /**
     * Deletes all Operations from a Resource
     *
     * @param apiId      Api with the Resource
     * @param resourceId Resource with the Operations
     */
    @Transactional
    public void deleteAllfromResource(String apiId, String resourceId) {
        List<Operation> operations = this.list(apiId, resourceId);
        operations.forEach(operation -> this.delete(apiId, resourceId, operation.getId()));
    }

    /*
     * A Operation can not have a single wild card at any point in it.
     *
     * @return  true when the path of the operation contains a single wild card, false otherwise
     */
    private boolean validateSingleWildCardOperationPath(Operation operation) {

        return Arrays.asList(operation.getPath().split("/")).contains("*");
    }

    /*
     * A Operation can have a one double wild card that must to be at the end of it, not at any other point.
     *
     * @return true when the path has more than one double wild card or one not at the end, false otherwise
     */
    private boolean validateDoubleWildCardOperationPath(Operation operation) {
        List<String> path = Arrays.asList(operation.getPath().split("/"));

        if (path.contains("**"))
            return !(operation.getPath().endsWith("**") && (path.stream().filter(o -> o.equals("**")).count() == 1));
        else
            return false;
    }

}
