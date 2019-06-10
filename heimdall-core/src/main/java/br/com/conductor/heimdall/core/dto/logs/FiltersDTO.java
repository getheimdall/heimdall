
package br.com.conductor.heimdall.core.dto.logs;

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

import lombok.Data;

/**
 * Trace filters for mongo search
 *
 * @author Marcelo Aguiar
 *
 */
@Data
public class FiltersDTO {

    private String name;

    private String type;

    private Operation operationSelected;

    private String firstValue;

    private String secondValue;

    public enum Operation {
        EQUALS, NOT_EQUALS, CONTAINS, BETWEEN, LESS_THAN, GREATER_THAN, LESS_THAN_EQUALS, GREATER_THAN_EQUALS,
        ALL, NONE, TODAY, YESTERDAY, THIS_WEEK, LAST_WEEK, THIS_MONTH, LAST_MONTH, THIS_YEAR
    }
}
