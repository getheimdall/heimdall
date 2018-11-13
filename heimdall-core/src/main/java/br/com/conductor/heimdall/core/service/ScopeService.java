/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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
package br.com.conductor.heimdall.core.service;

import br.com.conductor.heimdall.core.converter.GenericConverter;
import br.com.conductor.heimdall.core.dto.PageDTO;
import br.com.conductor.heimdall.core.dto.PageableDTO;
import br.com.conductor.heimdall.core.dto.ScopeDTO;
import br.com.conductor.heimdall.core.dto.page.ScopePage;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Scope;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.OperationRepository;
import br.com.conductor.heimdall.core.repository.PlanRepository;
import br.com.conductor.heimdall.core.repository.ScopeRepository;
import br.com.conductor.heimdall.core.util.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.*;

@Service
public class ScopeService {

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private PlanRepository planRepository;

    @Transactional(readOnly = true)
    public Scope find(final Long apiId, final Long scopeId) {

        final Scope scope = scopeRepository.findByApiIdAndId(apiId, scopeId);
        HeimdallException.checkThrow(scope == null, GLOBAL_RESOURCE_NOT_FOUND);

        return scope;
    }

    /**
     * Generates a paged list of {@link Scope} from a request.
     *
     * @param apiId       The {@link Api} Id
     * @param scopeDTO    The {@link ScopeDTO}
     * @param pageableDTO The {@link PageableDTO}
     * @return The paged {@link Scope} list as a {@link ScopePage} object
     */
    public ScopePage list(final Long apiId, final ScopeDTO scopeDTO, final PageableDTO pageableDTO) {

        Api api = apiRepository.findOne(apiId);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        Scope scope = GenericConverter.mapper(scopeDTO, Scope.class);
        scope.setApi(api);

        Example<Scope> example = Example.of(scope, ExampleMatcher.matching().withIgnorePaths("api.creationDate").withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        Pageable pageable = Pageable.setPageable(pageableDTO.getOffset(), pageableDTO.getLimit());
        Page<Scope> page = scopeRepository.findAll(example, pageable);

        return new ScopePage(PageDTO.build(page));
    }

    /**
     * Generates a list of {@link Scope} from a request.
     *
     * @param apiId    The {@link Api} Id
     * @param scopeDTO The {@link ScopeDTO}
     * @return The List of {@link Scope}
     */
    public List<Scope> list(final Long apiId, final ScopeDTO scopeDTO) {

        Api api = apiRepository.findOne(apiId);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        Scope scope = GenericConverter.mapper(scopeDTO, Scope.class);
        scope.setApi(api);

        Example<Scope> example = Example.of(scope, ExampleMatcher.matching().withIgnorePaths("api.creationDate").withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return scopeRepository.findAll(example);
    }

    @Transactional
    public Scope save(final Long apiId, Scope scope) {

        final Api api = apiRepository.findOne(apiId);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        final Scope scopeData = scopeRepository.findByApiIdAndName(apiId, scope.getName());
        HeimdallException.checkThrow(scopeData != null, SCOPE_INVALID_NAME);

        scope.getOperations().forEach(operation -> HeimdallException.checkThrow(
                operationRepository.findOne(operation.getId()) == null,
                SCOPE_INVALID_OPERATION,
                operation.getId().toString())
        );

        scope.getPlans().forEach(plan -> HeimdallException.checkThrow(
                planRepository.findOne(plan.getId()) == null,
                SCOPE_INVALID_PLAN,
                plan.getId().toString())
        );

        scope.setApi(api);

        scope = scopeRepository.save(scope);

        return scope;
    }

    public void delete(final Long apiId, final Long resourceId) {

        Scope scope = scopeRepository.findByApiIdAndId(apiId, resourceId);
        HeimdallException.checkThrow(scope == null, GLOBAL_RESOURCE_NOT_FOUND);

        scopeRepository.delete(scope.getId());
    }

    /**
     * Updates a {@link Scope} by its Id and {@link Api} Id
     *
     * @param apiId    The {@link Api} Id
     * @param scopeId  The {@link Scope} Id
     * @param scope The {@link Scope}
     * @return The updated {@link Scope}
     */
    public Scope update(final Long apiId, final Long scopeId, Scope scope) {

        Scope scopeByApiIdAndId = scopeRepository.findByApiIdAndId(apiId, scopeId);
        HeimdallException.checkThrow(scopeByApiIdAndId == null, GLOBAL_RESOURCE_NOT_FOUND);

        Scope scopeByApiIdAndName = scopeRepository.findByApiIdAndName(apiId, scope.getName());
        HeimdallException.checkThrow(scopeByApiIdAndName != null, ONLY_ONE_RESOURCE_PER_API);

        scope = scopeRepository.save(scope);

        return scope;
    }

}
