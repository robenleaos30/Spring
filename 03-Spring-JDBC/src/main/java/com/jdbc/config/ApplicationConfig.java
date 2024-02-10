package com.jdbc.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;

@Configuration
@PropertySource(value = "/database.properties")
@ComponentScan(basePackages = "com.jdbc.dao")
public class ApplicationConfig {

	@Value(value = "${db.url}")
	private String url;
	
	@Value(value = "${db.username}")
	private String user;

	@Value(value = "${db.password}")
	private String password;

	@Bean
	public DataSource dataSource() {
		var ds = new MysqlConnectionPoolDataSource();
		
		ds.setUrl(url);
		ds.setUser(user);
		ds.setPassword(password);
		
		return ds;
	}
	
	@Bean
	public JdbcTemplate template(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}
