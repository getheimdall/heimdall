/*-
 * =========================LICENSE_START==================================
 * heimdall-core
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
package br.com.heimdall.core.repository.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import br.com.heimdall.core.entity.Operation;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OperationJDBCRepository {

	private JdbcTemplate jdbcTemplate;

	public OperationJDBCRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Operation> findAllByApiId(Long id) {
		StringBuilder sql = new StringBuilder(170);
		sql.append("SELECT PATH ");
		sql.append("from OPERATIONS OP ");
		sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
		sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
		sql.append("WHERE API.ID = ? ");

		return jdbcTemplate.query(sql.toString(), new Object[] { id },
				new BeanPropertyRowMapper<Operation>(Operation.class));
	}

	public List<String> findOperationsFromAllApis(List<Long> apiIds) {

		Map<String, Object> params = new HashMap<>();
		params.put("ids", apiIds);
		
		StringBuilder sql = new StringBuilder(190);
		sql.append("SELECT CONCAT(API.BASE_PATH, OP.PATH) ");
		sql.append("FROM OPERATIONS OP ");
		sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
		sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
		sql.append("WHERE API.ID IN (:ids) ");

		return new NamedParameterJdbcTemplate(jdbcTemplate).queryForList(sql.toString(), params, String.class);
	}
	
	public List<Long> findIgnoredOperationsFromInterceptor(Long interceptorId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT OPERATION_ID ");
		sql.append("FROM IGNORED_INTERCEPTORS_OPERATIONS ");
		sql.append("WHERE INTERCEPTOR_ID = ? ");

		return jdbcTemplate.queryForList(sql.toString(), new Object[] { interceptorId }, Long.class);
	}
  
  public boolean patternExists(String pattern, Long apiId) {

		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT ");
		sql.append("count(*) ");
		sql.append("FROM OPERATIONS OP ");
		sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
		sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
	  sql.append("WHERE CONCAT(API.BASE_PATH, OP.PATH) = ? ");
		sql.append("AND API.ID <> ?");

		int count = jdbcTemplate.queryForObject(sql.toString(), new Object[] { pattern, apiId }, Integer.class);

		return count > 0;
	}
}
