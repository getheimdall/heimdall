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
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Scope;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ScopeRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPCacheService;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

/**
 * This class provides methods to create, read, update and delete the {@link Scope} entity.
 *
 * @author Marcelo Aguiar Rodrigues
 */
@Service
public class ScopeService {

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private AMQPCacheService amqpCacheService;

    @Transactional(readOnly = true)
    public Scope find(final String apiId, final String scopeId) {

        final Scope scope = scopeRepository.findByApiAndId(apiId, scopeId);
        HeimdallException.checkThrow(scope == null, GLOBAL_NOT_FOUND, "Scope");

        return scope;
    }

    /**
     * Generates a paged list of {@link Scope} from a request.
     *
     * @param apiId The {@link Api} Id
     * @return The paged {@link Scope} list
     */
    @Transactional(readOnly = true)
    public Page<Scope> list(final String apiId, final Pageable pageable) {

        final List<Scope> scopes = this.list(apiId);

        return new PageImpl<>(scopes, pageable, scopes.size());
    }

    /**
     * Generates a list of {@link Scope} from a request.
     *
     * @param apiId The {@link Api} Id
     * @return The List of {@link Scope}
     */
    @Transactional(readOnly = true)
    public List<Scope> list(final String apiId) {

        apiService.find(apiId);

        List<Scope> scopes = scopeRepository.findAll();
        scopes.sort(Comparator.comparing(Scope::getId));

        return scopes.stream()
                .filter(scope -> apiId.equals(scope.getApi()))
                .collect(Collectors.toList());
    }

    /**
     * Saves a {@link Scope} to the repository
     *
     * @param apiId Api Id
     * @param scope {@link Scope}
     * @return Scope saved
     */
    @Transactional
    public Scope save(final String apiId, Scope scope) {

        final Api api = apiService.find(apiId);

        validateScope(apiId, scope);

        scope.setApi(api.getId());

        scope = scopeRepository.save(scope);

        amqpCacheService.dispatchClean();

        return scope;
    }

    /**
     * Deletes a {@link Scope} from the repository.
     *
     * @param apiId   Api Id
     * @param scopeId Scope Id
     */
    @Transactional
    public void delete(final String apiId, final String scopeId) {

        Scope scope = this.find(apiId, scopeId);

        scopeRepository.delete(scope);

        amqpCacheService.dispatchClean();
    }

    /**
     * Updates a {@link Scope} by its Id and {@link Api} Id
     *
     * @param apiId   The {@link Api} Id
     * @param scopeId The {@link Scope} Id
     * @param scope   The {@link Scope}
     * @return The updated {@link Scope}
     */
    @Transactional
    public Scope update(final String apiId, final String scopeId, Scope scope) {

        Api api = apiService.find(apiId);

        this.find(apiId, scopeId);

        validateScope(apiId, scope);

        scope.setApi(api.getId());
        scope.setId(scopeId);

        scope = scopeRepository.save(scope);

        amqpCacheService.dispatchClean();

        return scope;
    }

    private void validateScope(String apiId, Scope scope) {
        final Scope scopeData = scopeRepository.findByApiAndName(apiId, scope.getName());
        HeimdallException.checkThrow(scopeData != null, SCOPE_INVALID_NAME);

        HeimdallException.checkThrow(scope.getOperations() == null || scope.getOperations().isEmpty(),
                SCOPE_NO_OPERATION_FOUND);

        scope.getOperations().forEach(op -> {
            Operation operation = operationService.find(op);

            HeimdallException.checkThrow(operation == null, SCOPE_INVALID_OPERATION, op);
            HeimdallException.checkThrow(!apiId.equals(operation.getApiId()), SCOPE_OPERATION_NOT_IN_API, op, apiId);
        });
    }

}
