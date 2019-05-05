package br.com.conductor.heimdall.core.repository.jdbc;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import br.com.conductor.heimdall.core.entity.Middleware;

@Repository
public class MiddlewareJDBCRepository {

	private JdbcTemplate jdbcTemplate;

	public MiddlewareJDBCRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Middleware> findAllActive() {
		StringBuilder sql = new StringBuilder(120);
		sql.append("SELECT * FROM MIDDLEWARES WHERE STATUS = 'ACTIVE'");

		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<Middleware>(Middleware.class));
	}
}
