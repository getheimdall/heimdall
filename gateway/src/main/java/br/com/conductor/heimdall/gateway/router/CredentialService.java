/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.conductor.heimdall.gateway.router;

import br.com.conductor.heimdall.core.entity.Api;
import br.com.conductor.heimdall.core.entity.Operation;
import br.com.conductor.heimdall.core.service.ApiService;
import br.com.conductor.heimdall.core.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private OperationService operationService;

    public List<Credential> findByPattern(String pattern) {

        final List<Api> apis = apiService.list();
        final List<Credential> credentials = new ArrayList<>();

        apis.forEach(api -> {
            final List<Operation> operations = operationService.list(api.getId());
            final List<Credential> collect = operations.stream()
                    .filter(operation -> pattern.equals(api.getBasePath() + operation.getPath()))
                    .map(operation -> {
                        Credential c = new Credential();
                        c.setApiBasePath(api.getBasePath());
                        c.setApiId(api.getId());
                        c.setApiName(api.getName());
                        c.setCors(api.isCors());
                        c.setOperationId(operation.getId());
                        c.setResourceId(operation.getResourceId());
                        c.setOperationPath(operation.getPath());
                        c.setMethod(operation.getMethod().toString());

                        return c;
                    })
                    .collect(Collectors.toList());
            credentials.addAll(collect);

        });

        return credentials;
//        StringBuilder sql = new StringBuilder(350);
//        sql.append("SELECT ");
//        sql.append("OP.METHOD AS METHOD, ");
//        sql.append("API.BASE_PATH AS APIBASEPATH, ");
//        sql.append("API.NAME AS APINAME, ");
//        sql.append("OP.ID AS OPERATIONID, ");
//        sql.append("RES.ID AS RESOURCEID, ");
//        sql.append("API.ID AS APIID, ");
//        sql.append("API.CORS AS CORS, ");
//        sql.append("OP.PATH AS OPERATIONPATH ");
//        sql.append("FROM OPERATIONS OP ");
//        sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
//        sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
//        sql.append("WHERE CONCAT(API.BASE_PATH, OP.PATH) = ? ");
//
//        return jdbcTemplate.query(sql.toString(), new Object[]{pattern}, new BeanPropertyRowMapper<>(Credential.class));
    }
}
