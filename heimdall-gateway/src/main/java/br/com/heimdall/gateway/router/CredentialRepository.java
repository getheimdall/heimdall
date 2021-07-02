/*-
 * =========================LICENSE_START==================================
 * heimdall-gateway
 * ========================================================================
 *
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
package br.com.heimdall.gateway.router;

import br.com.heimdall.core.util.ConstantsCache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CredentialRepository {

    private JdbcTemplate jdbcTemplate;

    public CredentialRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Cacheable(ConstantsCache.CREDENTIAL_ACTIVE_FROM_ENDPOINT)
    public List<Credential> findByPattern(String pattern) {
        StringBuilder sql = new StringBuilder(350);
        sql.append("SELECT ");
        sql.append("OP.METHOD AS METHOD, ");
        sql.append("API.BASE_PATH AS APIBASEPATH, ");
        sql.append("API.NAME AS APINAME, ");
        sql.append("OP.ID AS OPERATIONID, ");
        sql.append("RES.ID AS RESOURCEID, ");
        sql.append("API.ID AS APIID, ");
        sql.append("API.CORS AS CORS, ");
        sql.append("OP.PATH AS OPERATIONPATH ");
        sql.append("FROM OPERATIONS OP ");
        sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
        sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
        sql.append("WHERE CONCAT(API.BASE_PATH, OP.PATH) = ? ");

        return jdbcTemplate.query(sql.toString(), new Object[]{pattern}, new BeanPropertyRowMapper<>(Credential.class));
    }
}
