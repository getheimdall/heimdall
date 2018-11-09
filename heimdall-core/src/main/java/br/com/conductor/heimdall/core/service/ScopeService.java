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
import br.com.conductor.heimdall.core.dto.ScopeDTO;
import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Scope;
import br.com.conductor.heimdall.core.exception.HeimdallException;
import br.com.conductor.heimdall.core.repository.ApiRepository;
import br.com.conductor.heimdall.core.repository.ScopeRepository;
import br.com.conductor.heimdall.core.service.amqp.AMQPRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static br.com.conductor.heimdall.core.exception.ExceptionMessage.GLOBAL_RESOURCE_NOT_FOUND;
import static br.com.conductor.heimdall.core.exception.ExceptionMessage.ONLY_ONE_RESOURCE_PER_API;

@Service
public class ScopeService {

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private AMQPRouteService amqpRoute;

    @Transactional(readOnly = true)
    public Scope find(final Long apiId, final Long scopeId) {

        final Scope scope = scopeRepository.findByApiIdAndId(apiId, scopeId);
        HeimdallException.checkThrow(scope == null, GLOBAL_RESOURCE_NOT_FOUND);

        return scope;
    }

    public Scope save(Long apiId, ScopeDTO scopeDTO) {
        Api api = apiRepository.findOne(apiId);
        HeimdallException.checkThrow(api == null, GLOBAL_RESOURCE_NOT_FOUND);

        Scope scopeData = scopeRepository.findByApiIdAndName(apiId, scopeDTO.getName());
        HeimdallException.checkThrow(scopeData != null && (Objects.equals(scopeData.getApi().getId(), api.getId())), ONLY_ONE_RESOURCE_PER_API);

        Scope scope = GenericConverter.mapper(scopeDTO, Scope.class);
        scope.setApi(api);

        scope = scopeRepository.save(scope);

        amqpRoute.dispatchRoutes();

        return scope;
    }

    public void delete(Long apiId, Long resourceId) {

        Scope scope = scopeRepository.findByApiIdAndId(apiId, resourceId);
        HeimdallException.checkThrow(scope == null, GLOBAL_RESOURCE_NOT_FOUND);

        scopeRepository.delete(scope.getId());

        amqpRoute.dispatchRoutes();
    }

}
