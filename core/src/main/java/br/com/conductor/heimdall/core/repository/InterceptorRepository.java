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
package br.com.conductor.heimdall.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Resource;

/**
 * Provides methods to access a {@link Interceptor}
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
public interface InterceptorRepository extends JpaRepository<Interceptor, String> {

    @EntityGraph(attributePaths = {"ignoredOperations"})
    @Override
    List<Interceptor> findAll();

    /**
     * Finds all Interceptors by {@link Operation} Id.
     *
     * @param operationId The Operation Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findAllByOperationId(String operationId);

    /**
     * Finds all Interceptors by {@link Resource} Id.
     *
     * @param resourceId The Resource Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findAllByResourceId(String resourceId);

    List<Interceptor> findAllByPlanId(String planId);

    List<Interceptor> findAllByApiId(String apiId);

}
