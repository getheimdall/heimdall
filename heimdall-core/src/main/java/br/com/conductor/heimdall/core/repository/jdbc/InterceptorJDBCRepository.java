///*
// * Copyright (C) 2018 Conductor Tecnologia SA
// *
// * Licensed under the Apache License, Version 2.0 (the "License")
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package br.com.conductor.heimdall.core.repository.jdbc;
//
//import br.com.conductor.heimdall.core.entity.Api;
//import br.com.conductor.heimdall.core.entity.Interceptor;
//import br.com.conductor.heimdall.core.enums.InterceptorLifeCycle;
//import br.com.conductor.heimdall.core.enums.TypeExecutionPoint;
//import br.com.conductor.heimdall.core.enums.TypeInterceptor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.util.List;
//
//@Repository
//public class InterceptorJDBCRepository {
//
//    private JdbcTemplate jdbcTemplate;
//
//    public InterceptorJDBCRepository(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }
//
//    public List<Interceptor> findAllInterceptorsSimplified() {
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT I.ID, I.API_ID, I.EXECUTION_POINT, I.LIFE_CYCLE, I.TYPE, I.NAME, I.CONTENT, I.EXECUTION_ORDER, I.STATUS, ");
//        sql.append("CASE ");
//        sql.append("    WHEN LIFE_CYCLE = 'OPERATION' THEN I.OPERATION_ID ");
//        sql.append("    WHEN LIFE_CYCLE = 'RESOURCE' THEN I.RESOURCE_ID ");
//        sql.append("    WHEN LIFE_CYCLE = 'PLAN' THEN I.PLAN_ID ");
//        sql.append("    ELSE I.API_ID ");
//        sql.append("END AS REFERENCEID ");
//        sql.append("FROM INTERCEPTORS I");
//
//        return jdbcTemplate.query(sql.toString(), (resultSet, i) -> {
//            Interceptor interceptor = new Interceptor();
//            interceptor.setId(resultSet.getString(1));
//            Api api = new Api();
//            api.setId(resultSet.getString(2));
////            interceptor.setApi(api);
//            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
//            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
//            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));
//            interceptor.setName(resultSet.getString(6));
//            interceptor.setContent(resultSet.getString(7));
//            interceptor.setOrder(resultSet.getInt(8));
//            interceptor.setStatus(resultSet.getBoolean(9));
//            interceptor.setReferenceId(resultSet.getString(10));
//
//            return interceptor;
//        });
//    }
//
//    public List<Interceptor> findInterceptorsSimplifiedFromMiddleware(Long middlewareId) {
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT I.ID, I.API_ID, I.EXECUTION_POINT, I.LIFE_CYCLE, I.TYPE, I.NAME, I.CONTENT, I.EXECUTION_ORDER, I.STATUS, ");
//        sql.append("CASE ");
//        sql.append("    WHEN LIFE_CYCLE = 'OPERATION' THEN I.OPERATION_ID ");
//        sql.append("    WHEN LIFE_CYCLE = 'RESOURCE' THEN I.RESOURCE_ID ");
//        sql.append("    WHEN LIFE_CYCLE = 'PLAN' THEN I.PLAN_ID ");
//        sql.append("    ELSE I.API_ID ");
//        sql.append("END AS REFERENCEID ");
//        sql.append("FROM INTERCEPTORS I ");
//        sql.append("INNER JOIN MIDDLEWARES_INTERCEPTORS MID ON MID.INTERCEPTOR_ID = I.ID ");
//        sql.append("WHERE MID.MIDDLEWARE_ID = ? ");
//
//        return jdbcTemplate.query(sql.toString(), new Object[] { middlewareId }, (resultSet, i) -> {
//            Interceptor interceptor = new Interceptor();
//            interceptor.setId(resultSet.getString(1));
//            Api api = new Api();
//            api.setId(resultSet.getString(2));
////            interceptor.setApi(api);
//            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
//            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
//            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));
//            interceptor.setName(resultSet.getString(6));
//            interceptor.setContent(resultSet.getString(7));
//            interceptor.setOrder(resultSet.getInt(8));
//            interceptor.setStatus(resultSet.getBoolean(9));
//            interceptor.setReferenceId(resultSet.getString(10));
//
//            return interceptor;
//        });
//    }
//
//    public Interceptor findOneInterceptorSimplified(Long interceptorId) {
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT I.ID, I.API_ID, I.EXECUTION_POINT, I.LIFE_CYCLE, I.TYPE, I.NAME, I.CONTENT, I.EXECUTION_ORDER, I.STATUS, ");
//        sql.append("CASE ");
//        sql.append("    WHEN LIFE_CYCLE = 'OPERATION' THEN I.OPERATION_ID ");
//        sql.append("    WHEN LIFE_CYCLE = 'RESOURCE' THEN I.RESOURCE_ID ");
//        sql.append("    WHEN LIFE_CYCLE = 'PLAN' THEN I.PLAN_ID ");
//        sql.append("    ELSE I.API_ID ");
//        sql.append("END AS REFERENCEID ");
//        sql.append("FROM INTERCEPTORS I ");
//        sql.append("WHERE I.ID = ?");
//
//        return jdbcTemplate.queryForObject(sql.toString(), new Object[] { interceptorId }, (resultSet, i) -> {
//            Interceptor interceptor = new Interceptor();
//            interceptor.setId(resultSet.getString(1));
//            Api api = new Api();
//            api.setId(resultSet.getString(2));
////            interceptor.setApi(api);
//            interceptor.setExecutionPoint(TypeExecutionPoint.valueOf(resultSet.getString(3)));
//            interceptor.setLifeCycle(InterceptorLifeCycle.valueOf(resultSet.getString(4)));
//            interceptor.setType(TypeInterceptor.valueOf(resultSet.getString(5)));
//            interceptor.setName(resultSet.getString(6));
//            interceptor.setContent(resultSet.getString(7));
//            interceptor.setOrder(resultSet.getInt(8));
//            interceptor.setStatus(resultSet.getBoolean(9));
//            interceptor.setReferenceId(resultSet.getString(10));
//
//            return interceptor;
//        });
//    }
//}
