/*
 * Copyright (C) 2018 Conductor Tecnologia SA
 *
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
 */
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
