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

package br.com.conductor.heimdall.core.repository.jdbc;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Interceptor;
import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
import br.com.conductor.heimdall.core.enums.TypeInterceptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class InterceptorJDBCRepository {

    private JdbcTemplate jdbcTemplate;

    public InterceptorJDBCRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Interceptor> findAllInterceptorsSimplified() {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT i.id, i.api_id, i.execution_point, i.life_cycle, i.type ");
        sql.append("FROM INTERCEPTORS i");

        return jdbcTemplate.query(sql.toString(), (resultSet, i) -> {
            Interceptor interceptor = new Interceptor();
            interceptor.setId(resultSet.getLong(1));
            Api api = new Api();
            api.setId(resultSet.getLong(2));
            interceptor.setApi(api);
            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));

            return interceptor;
        });
    }
}
