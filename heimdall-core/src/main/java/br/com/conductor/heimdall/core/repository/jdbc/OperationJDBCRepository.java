package br.com.conductor.heimdall.core.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OperationJDBCRepository {

	private JdbcTemplate jdbcTemplate;

	public OperationJDBCRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
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

	public boolean patternExists(String pattern) {

		StringBuilder sql = new StringBuilder(180);
		sql.append("SELECT ");
		sql.append("count(*) ");
		sql.append("FROM OPERATIONS OP ");
		sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
		sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
		sql.append("WHERE CONCAT(API.BASE_PATH, OP.PATH) = ?");

		int count = jdbcTemplate.queryForObject(sql.toString(), new Object[] { pattern }, Integer.class);

		return count > 0;
	}
}
