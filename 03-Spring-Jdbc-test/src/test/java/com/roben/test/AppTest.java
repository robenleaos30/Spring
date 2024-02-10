package com.roben.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.roben.config.ApplicationConfig;
import com.roben.dao.MemberDao;
import com.roben.dto.Member;

@TestMethodOrder(OrderAnnotation.class)
@SpringJUnitConfig(classes = ApplicationConfig.class)
public class AppTest {

	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private JdbcOperations jdbcOp;
	
	@Test
	@Order(1)
	@Sql(scripts = "/schema.sql")
	void test() {
		Member m = new Member();
		m.setLoginId("1");
		m.setPassword("12345");
		m.setName("admin");
		
		memberDao.create(m);
	}
	
	@Test
	@Order(2)
	void memberTest() {
		var data = jdbcOp.execute((Connection conn) -> {
			var stat = conn.createStatement();
			var rs = stat.executeQuery("select count(*) from member");
			while(rs.next()) {
				return rs.getLong(1);
			}
			return 0;
		});
		
		assertEquals(1L, data);
	}
}
