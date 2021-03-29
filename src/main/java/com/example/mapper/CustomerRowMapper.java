package com.example.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.model.Customer;
import com.example.model.CustomerNew;

public class CustomerRowMapper implements RowMapper<CustomerNew>{

	  @Override
	  public CustomerNew mapRow(ResultSet rs, int rowNum) throws SQLException {
		  CustomerNew user = new CustomerNew();
	   user.setId(rs.getLong("id"));
	   user.setFirstName(rs.getString("name"));
	   user.setLastName("Arun");
	   return user;
	  }

}
