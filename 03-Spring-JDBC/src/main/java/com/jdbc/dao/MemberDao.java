package com.jdbc.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.jdbc.dto.Member;

@Component
public class MemberDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void create(Member member) {
		jdbcTemplate.update("insert into MEMBER values (?,?,?,?,?)",
				member.getLoginId(), member.getPassword(), member.getName(),
				member.getPhone(), member.getEmail());
	}
}
