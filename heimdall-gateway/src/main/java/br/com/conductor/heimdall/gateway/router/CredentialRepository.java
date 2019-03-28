package br.com.conductor.heimdall.gateway.router;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.conductor.heimdall.core.util.ConstantsCache;

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

		return jdbcTemplate.query(sql.toString(), new Object[] { pattern }, new BeanPropertyRowMapper<Credential>(Credential.class));
	}
}
