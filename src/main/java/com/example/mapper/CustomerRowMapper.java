package com.example.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.model.Customer;

public class CustomerRowMapper implements RowMapper<Customer>{

	  @Override
	  public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		  Customer user = new Customer();
	   user.setId(rs.getLong("id"));
	   user.setFirstName(rs.getString("name"));
	   user.setLastName("Arun");
	   user.setBirthdate(rs.getDate("birthdate"));
	   return user;
	  }

}
