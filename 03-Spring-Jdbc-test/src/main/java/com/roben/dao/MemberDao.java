package com.roben.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.roben.dto.Member;

@Component
public class MemberDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void create(Member member) {
		jdbcTemplate.update("insert into member values (?,?,?,?,?)",
				member.getLoginId(), member.getPassword(), member.getName(),
				member.getPhone(), member.getEmail());
	}
}
