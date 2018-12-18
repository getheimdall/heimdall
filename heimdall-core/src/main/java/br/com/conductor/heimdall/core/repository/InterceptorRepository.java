
package br.com.conductor.heimdall.core.repository;

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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.entity.Plan;
import br.com.conductor.heimdall.core.entity.Resource;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;

/**
 * Provides methods to access a {@link Interceptor}
 *
 * @author Filipe Germano
 * @author Marcelo Aguiar Rodrigues
 */
public interface InterceptorRepository extends JpaRepository<Interceptor, Long> {

    /**
     * Finds a List of Interceptors by Interceptor type and {@link Api} Id.
     * @param type  The type of Interceptor
     * @param apiId The Api Id.
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByTypeAndApiId(TypeInterceptor type, Long apiId);

    /**
     * Finds a List of Interceptors by Interceptor type and {@link Plan} Id.
     *
     * @param type   The type of Interceptor
     * @param planId The Plan Id.
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByTypeAndPlanId(TypeInterceptor type, Long planId);

    /**
     * Finds a List of Interceptors by Interceptor type and {@link Resource} Id.
     *
     * @param type       The type of Interceptor
     * @param resourceId The Resource Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByTypeAndResourceId(TypeInterceptor type, Long resourceId);

    /**
     * Finds a List of Interceptors by Interceptor type and {@link Operation} Id.
     *
     * @param type        The type of Interceptor
     * @param operationId The Operation Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByTypeAndOperationId(TypeInterceptor type, Long operationId);

    /**
     * Finds a List of Interceptors by Interceptor type and {@link Api} Id.
     *
     * @param type  The type of Interceptor
     * @param apiId The Api Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByTypeAndOperationResourceApiId(TypeInterceptor type, Long apiId);

    /**
     * Finds all Interceptors by {@link Operation} Id.
     *
     * @param operationId The Operation Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByOperationId(Long operationId);

    /**
     * Finds all Interceptors by {@link Resource} Id.
     *
     * @param resourceId The Resource Id
     * @return The List of Interceptor associated
     */
    List<Interceptor> findByResourceId(Long resourceId);

}
