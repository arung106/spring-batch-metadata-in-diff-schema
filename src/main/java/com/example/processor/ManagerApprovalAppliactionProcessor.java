package com.example.processor;

import org.springframework.batch.item.ItemProcessor;

import com.example.model.CustomerNew;

public class ManagerApprovalAppliactionProcessor implements ItemProcessor<CustomerNew, CustomerNew> {  

	@Override 
	public CustomerNew process(CustomerNew customer) throws Exception {  
		customer.setLastName("Manju"); 
		return customer; 
	}

}
