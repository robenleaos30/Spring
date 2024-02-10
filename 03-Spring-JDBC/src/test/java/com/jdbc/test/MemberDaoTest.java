package com.jdbc.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.jdbc.config.ApplicationConfig;
import com.jdbc.dao.MemberDao;
import com.jdbc.dto.Member;

@TestMethodOrder(OrderAnnotation.class)
@SpringJUnitConfig(classes = ApplicationConfig.class)
public class MemberDaoTest {

	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private JdbcOperations jdbcOp;
	
	@Test
	@Sql(scripts = "/database.sql")
	@Order(1)
	void test() {
		Member m = new Member();
		m.setLoginId("admin");
		m.setPassword("admins");
		m.setName("Admin");
		
		memberDao.create(m);
	}
	
	@Test
	@Order(2)
	void testJdbcOp() {
		var data = jdbcOp.execute((Connection conn) -> {
			var stat = conn.createStatement();
			var rs = stat.executeQuery("select count(*) from MEMBER");
			while (rs.next()) {
				return rs.getLong(1);
			}
			return 0;
		});
		Assertions.assertEquals(1L, data);
	}
	
	@Test
	@Order(3)
	void testStat() {
		var data = jdbcOp.execute((Statement stat) -> {
			return stat.executeUpdate("""
					insert into MEMBER (loginId, password, name) values ('2', 'password', 'name')
					""");
		});
		assertEquals(1, data);
	}
	
	@Test
	@Order(4)
	void testStaticQuaryRowExtractor() {
		var data = jdbcOp.query("select count(*) from MEMBER", rs -> {
			while(rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		});
		assertEquals(2, data);
		
		var data1 = jdbcOp.query("select * from MEMBER", rs -> {
			var list = new ArrayList<>();
			while(rs.next()) {
				var m = new Member();
				m.setLoginId(rs.getString(1));
				m.setPassword(rs.getString(2));
				m.setName(rs.getString(3));
				m.setPhone(rs.getString(4));
				m.setEmail(rs.getString(5));
				list.add(m);
			}
			return list;
		});
		assertEquals(2, data1.size());
	}
	
	@Test
	@Order(5)
	void test5() {
		
		var list = new ArrayList<Member>();	
		
		jdbcOp.query("select * from MEMBER", rs -> {
			
			var m = new Member();
			
			m.setLoginId(rs.getString(1));
			m.setPassword(rs.getString(2));
			m.setName(rs.getString(3));
			m.setPhone(rs.getString(4));
			m.setEmail(rs.getString(5));
						
			list.add(m);
		});
		assertEquals(2, list.size());
	}
	
	@Test
	@Order(6)
	void test6() {
		
		var list = jdbcOp.query("select * from MEMBER", 
				(rs, no) -> {
					var m = new Member();
					m.setLoginId(rs.getString(1));
					m.setPassword(rs.getString(2));
					m.setName(rs.getString(3));
					m.setPhone(rs.getString(4));
					m.setEmail(rs.getString(5));
					return m;
				}
			);
		assertEquals(2, list.size());
	}
	
	@Test
	@Order(7)
	void testStaticResultQuery() {
		var admin = jdbcOp.queryForObject("select name from MEMBER where loginId = 'admin'", String.class);
		assertEquals("Admin", admin);
	}
}










