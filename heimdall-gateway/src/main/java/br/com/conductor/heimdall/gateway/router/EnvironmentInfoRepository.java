/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
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
package br.com.conductor.heimdall.gateway.router;

import br.com.conductor.heimdall.core.util.ConstantsCache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EnvironmentInfoRepository {

    private JdbcTemplate jdbcTemplate;

    public EnvironmentInfoRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Cacheable(cacheNames = ConstantsCache.ENVIRONMENT_ACTIVE_CACHE, key = "#apiId.toString() + ':' + #inboundURL")
    public EnvironmentInfo findByApiIdAndEnvironmentInboundURL(Long apiId, String inboundURL) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT EN.ID, EN.OUTBOUND_URL as OUTBOUNDURL FROM ENVIRONMENTS AS EN ");
        sql.append("INNER JOIN APIS_ENVIRONMENTS AE ON EN.ID = AE.ENVIRONMENT_ID ");
        sql.append("WHERE AE.API_ID = ");
        sql.append(apiId);
        sql.append(" AND EN.INBOUND_URL LIKE '%");
        sql.append(inboundURL);
        sql.append("%'");

        final List<EnvironmentInfo> environmentInfos = jdbcTemplate.query(sql.toString(), (resultSet, ignored) -> {
            EnvironmentInfo env = new EnvironmentInfo();
            env.setId(resultSet.getLong("id"));
            env.setOutboundURL(resultSet.getString("outboundURL"));
            return env;
        });

        EnvironmentInfo environment = null;

        if (!environmentInfos.isEmpty()) {
            environment = environmentInfos.get(0);
        }

        if (environment != null) {

            String getVariables = "select \"key\", value from variables where environment_id = " + environment.getId();

            Map variables = jdbcTemplate.query(getVariables, resultSetExtractor -> {
                Map<String, String> result = new HashMap<>();
                while (resultSetExtractor.next()) {
                    result.put(
                            resultSetExtractor.getString("key"),
                            resultSetExtractor.getString("value")
                    );
                }
                return result;
            });

            environment.setVariables(variables);

            return environment;
        } else {
            return null;
        }
    }

}
