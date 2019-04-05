package br.com.conductor.heimdall.core.repository.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.conductor.heimdall.core.entity.Api;

@Repository
public class ApiJDBCRepository {

	private JdbcTemplate jdbcTemplate;

	public ApiJDBCRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Long> findAllIds() {
		StringBuilder sql = new StringBuilder(30);
		sql.append("SELECT ID FROM APIS");

		return jdbcTemplate.queryForList(sql.toString(), Long.class);
	}

	public List<Api> findAll() {
		StringBuilder sql = new StringBuilder(120);
		sql.append("SELECT ID, BASE_PATH FROM APIS");

		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<Api>(Api.class));
	}
}
