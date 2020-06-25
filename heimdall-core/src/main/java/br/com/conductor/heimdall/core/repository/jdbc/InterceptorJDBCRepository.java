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

    private static final String SQL = "SELECT I.ID, I.API_ID, I.EXECUTION_POINT, I.LIFE_CYCLE, I.TYPE, I.NAME, I.CONTENT, I.EXECUTION_ORDER, I.STATUS, " +
                                        "CASE " +
                                        "   WHEN LIFE_CYCLE = 'OPERATION' THEN I.OPERATION_ID " +
                                        "   WHEN LIFE_CYCLE = 'RESOURCE' THEN I.RESOURCE_ID " +
                                        "   WHEN LIFE_CYCLE = 'PLAN' THEN I.PLAN_ID " +
                                        "   ELSE I.API_ID " +
                                        "END AS REFERENCEID " +
                                        "FROM INTERCEPTORS I";
    private static final String FINDINTERCEPTORSFROMMIDDLEWARE = SQL + "INNER JOIN MIDDLEWARES_INTERCEPTORS MID ON MID.INTERCEPTOR_ID = I.ID " +
                                                                "WHERE MID.MIDDLEWARE_ID = ? ";
    private static final String FINDONEINTERCEPTOR = SQL + "WHERE I.ID = ?";


    public InterceptorJDBCRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Interceptor> findAllInterceptorsSimplified() {

        return jdbcTemplate.query(SQL, (resultSet, i) -> {
            Interceptor interceptor = new Interceptor();
            interceptor.setId(resultSet.getLong(1));
            Api api = new Api();
            api.setId(resultSet.getLong(2));
            interceptor.setApi(api);
            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));
            interceptor.setName(resultSet.getString(6));
            interceptor.setContent(resultSet.getString(7));
            interceptor.setOrder(resultSet.getInt(8));
            interceptor.setStatus(resultSet.getBoolean(9));
            interceptor.setReferenceId(resultSet.getLong(10));

            return interceptor;
        });
    }
    
    public List<Interceptor> findInterceptorsSimplifiedFromMiddleware(Long middlewareId) {

        return jdbcTemplate.query(FINDINTERCEPTORSFROMMIDDLEWARE, new Object[] { middlewareId }, (resultSet, i) -> {
            Interceptor interceptor = new Interceptor();
            interceptor.setId(resultSet.getLong(1));
            Api api = new Api();
            api.setId(resultSet.getLong(2));
            interceptor.setApi(api);
            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));
            interceptor.setName(resultSet.getString(6));
            interceptor.setContent(resultSet.getString(7));
            interceptor.setOrder(resultSet.getInt(8));
            interceptor.setStatus(resultSet.getBoolean(9));
            interceptor.setReferenceId(resultSet.getLong(10));

            return interceptor;
        });
    }
    
    public Interceptor findOneInterceptorSimplified(Long interceptorId) {


        return jdbcTemplate.queryForObject(FINDONEINTERCEPTOR, new Object[] { interceptorId }, (resultSet, i) -> {
            Interceptor interceptor = new Interceptor();
            interceptor.setId(resultSet.getLong(1));
            Api api = new Api();
            api.setId(resultSet.getLong(2));
            interceptor.setApi(api);
            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));
            interceptor.setName(resultSet.getString(6));
            interceptor.setContent(resultSet.getString(7));
            interceptor.setOrder(resultSet.getInt(8));
            interceptor.setStatus(resultSet.getBoolean(9));
            interceptor.setReferenceId(resultSet.getLong(10));

            return interceptor;
        });
    }
}
