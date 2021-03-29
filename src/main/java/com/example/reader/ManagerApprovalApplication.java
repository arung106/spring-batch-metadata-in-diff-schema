package com.example.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.example.model.CustomerNew;


public class ManagerApprovalApplication implements ItemReader<CustomerNew> {

	@Override
	public CustomerNew read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		CustomerNew cn = new CustomerNew();
		cn.setId(1L);	;
		cn.setFirstName("Arun");
		cn.setLastName("G");
		return null;
	}

}
