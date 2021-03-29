package com.example.mapper;

import java.time.LocalDate;
import java.util.Date;

import javax.annotation.sql.DataSourceDefinition;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.example.model.Customer;

import lombok.Builder;
import lombok.Data;


public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

	@Override
	public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
		Customer cust = new Customer();
		cust.setId(fieldSet.readLong("id"));
		cust.setFirstName(fieldSet.readRawString("firstName"));
		cust.setLastName(fieldSet.readRawString("lastName"));
		cust.setBirthdate(fieldSet.readDate("birthdate", "dd-MM-yyyy HH:mm:ss"));
		
		return cust;
	}
}
