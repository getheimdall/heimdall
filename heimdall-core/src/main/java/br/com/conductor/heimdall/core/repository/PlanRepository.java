
package br.com.conductor.heimdall.core.repository;

/*-
 * =========================LICENSE_START==================================
 * heimdall-core
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
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
 * ==========================LICENSE_END===================================
 */

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conductor.heimdall.core.entity.Plan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Plan Repository.
 * 
 * @author Filipe Germano
 *
 */
public interface PlanRepository extends JpaRepository<Plan, Long> {

     /**
      * Check if a plan has apps attached
      *
      * @param id
      * @return
      */
     @Query(value = "select count(0) from apps_plans where plan_id = :id", nativeQuery = true)
     Integer findAppsWithPlan(@Param("id") Long id);

}
