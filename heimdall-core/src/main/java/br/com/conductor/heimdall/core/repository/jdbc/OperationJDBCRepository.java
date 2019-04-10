package br.com.conductor.heimdall.core.repository.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.conductor.heimdall.core.entity.Operation;

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

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ids", apiIds);
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT CONCAT(API.BASE_PATH, OP.PATH) ");
		sql.append("FROM OPERATIONS OP ");
		sql.append("INNER JOIN RESOURCES RES ON OP.RESOURCE_ID = RES.ID ");
		sql.append("INNER JOIN APIS API ON RES.API_ID = API.ID ");
		sql.append("WHERE API.ID IN (:ids) ");

		return new NamedParameterJdbcTemplate(jdbcTemplate).queryForList(sql.toString(), params, String.class);
	}
}
