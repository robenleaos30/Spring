package com.jdbc.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.jdbc.dto.Member;

@SpringJUnitConfig(locations = "/application-context.xml")
@TestMethodOrder(OrderAnnotation.class)
public class PropertyTest {
	
	@Autowired
	private JdbcOperations jdbc;
	
	@Autowired
	@Qualifier("memberRowMapper")
	private RowMapper<Member> memberRowMapper;
	
	@Test
	@Order(1)
	@Sql(scripts = "/database.sql")
	@DisplayName(value = "1. Execute with Creator")
	void test(@Qualifier("memberInsert") PreparedStatementCreatorFactory count) {
				
		var creator = count.newPreparedStatementCreator(List.of(
				"admin", "admin", "Admin User", "09774837593", "admin@gmail.com"
				));
		
		var last = jdbc.execute(creator, PreparedStatement::executeUpdate);
		
		assertEquals(1, last);
	}
	
	
	@Test
	@Order(2)
	@DisplayName(value = "2. Update with Creator")
	void test1(@Qualifier("memberInsert") PreparedStatementCreatorFactory count) {
				
		var creator = count.newPreparedStatementCreator(List.of(
				"member", "member", "member user", "09774837593", "member@gmail.com"
				));
		
		var last = jdbc.update(creator);
		
		assertEquals(1, last);
	}
	
	@Test
	@Order(3)
	@DisplayName(value = "3.Execute With Find By Name")
	void test2(@Qualifier("memberFindByNameLike") PreparedStatementCreatorFactory count) {
						
		var last = jdbc.execute(count.newPreparedStatementCreator(List.of("member%")),
				stmt -> {
					var list = new ArrayList<>();
					var rs = stmt.executeQuery();
					
					while(rs.next()) {
						list.add(memberRowMapper);
					}
					
					return list;
				});
		assertEquals(1, last.size());
	}
	
	@Test
	@Order(4)
	@DisplayName(value = "4.Query With Find By Name")
	void test3(@Qualifier("memberFindByNameLike") PreparedStatementCreatorFactory count) {
								
		var last = jdbc.query(count.newPreparedStatementCreator(List.of("member%")), memberRowMapper);
		assertEquals(1, last.size());
	}
	
	@Test
	@Order(5)
	@DisplayName(value = "5.Query With Find By Pk")
	void test4(@Qualifier("memberFindByPkLike") PreparedStatementCreatorFactory count) {
								
		var result = jdbc.query(count.newPreparedStatementCreator(List.of("member")), rs -> {
			while(rs.next()) {
				return memberRowMapper.mapRow(rs, 1);
			}
			return null;
		});
		assertNotNull(result);
		assertEquals("member@gmail.com", result.getEmail());
	}
	
	@Test
	@Order(6)
	@DisplayName(value = "6.Member Insert with SQL string")
	void test5(@Value("${member.insert}") String sql) {
		var count = jdbc.update(sql, stmt -> {
			stmt.setString(1, "3");
			stmt.setString(2, "123456");
			stmt.setString(3, "aung");
			stmt.setString(4, "097365768362");
			stmt.setString(5, "aung@gmail.com");
		});
		assertEquals(1, count);
	}
	
	@Test
	@Order(7)
	@DisplayName(value = "7.Member Insert with SQL string update with only insert")
	void test6(@Value("${member.insert}") String sql) {
		var count = jdbc.update(sql, "4", "23456", "min", "094747583824", "min@gmail.com");
		assertEquals(1, count);
	}
	
	@Test
	@Order(8)
	@DisplayName(value = "8.Query with parameterstatememt")
	void test7(@Value("${member.select.by.name}") String sql) {
		var list = jdbc.query(sql, stmt -> stmt.setString(1, "min%"), memberRowMapper);
		assertEquals(1, list.size());
	}
	
	@Test
	@Order(9)
	@DisplayName(value = "9.Query with params")
	void test8(@Value("${member.select.by.name}") String sql) {
		var list = jdbc.query(sql, memberRowMapper, "aung%");
		assertEquals(1, list.size());
	}
	
	@Test
	@Order(10)
	@DisplayName(value = "10.Select one with prepareStatememtCreator")
	void test10(@Value("${member.select.by.pk}") String sql) {
		var list = jdbc.query(sql, stmt -> stmt.setString(1, "3"), rs -> {
			while(rs.next()) {
				return memberRowMapper.mapRow(rs, 1);
			}
			return null;	
		});
		assertEquals("aung", list.getName());
	}
	
	@Test
	@Order(12)
	@DisplayName(value = "12.Select one with params")
	void test12(@Value("${member.select.by.pk}") String sql) {
		var list = jdbc.query(sql, rs -> {
			while(rs.next()) {
				return memberRowMapper.mapRow(rs, 1);
			}
			return null;
		}, "3");	
		assertEquals("aung", list.getName());
	}
	
	@Test
	@Order(13)
	@DisplayName(value = "13.Select one with object")
	void test13(@Value("${member.select.by.pk}") String sql) {
		var list = jdbc.queryForObject(sql, memberRowMapper, "4");
		assertEquals("min", list.getName());
	}
	
	@Test
	@Order(14)
	@DisplayName(value = "14.Select Single object with SQL")
	void test14() {
		var sql = "select count(*) from MEMBER where name like ?";
		var count = jdbc.queryForObject(sql, Integer.class, "min%");
		assertEquals(1, count);
	}
}


