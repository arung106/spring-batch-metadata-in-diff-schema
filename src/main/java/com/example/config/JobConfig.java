package com.example.config;


import java.sql.Date;

import javax.batch.api.chunk.ItemProcessor;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.job.incrmentor.SampleIncrementer;
import com.example.mapper.CustomerFieldSetMapper;
import com.example.mapper.CustomerRowMapper;
import com.example.model.Customer;
import com.example.model.CustomerNew;
import com.example.processor.CustomerProcessor;
import com.example.processor.ManagerApprovalAppliactionProcessor;
import com.example.reader.ManagerApprovalAppliactionReader;
import com.example.writer.ManagerApprovalApplicationWriter;

@Configuration
public class JobConfig {
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Qualifier("secondaryDS")
	@Autowired
	private DataSource dataSource;
	
	
	@Bean
	public FlatFileItemReader<Customer> customerItemReader(){
		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new ClassPathResource("/data/customer.csv"));
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"});
		
		DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
		customerLineMapper.setLineTokenizer(tokenizer);
		customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
		customerLineMapper.afterPropertiesSet();
		
		reader.setLineMapper(customerLineMapper);
		
		return reader;
	}
	
	@Bean
	public JdbcBatchItemWriter<Customer> customerItemWriter(){
		JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(this.dataSource);
		writer.setSql("INSERT INTO CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.afterPropertiesSet();
		
		return writer;
	}
	
	
	@Bean
	@StepScope
	public JdbcCursorItemReader<CustomerNew> itemReader() {
		return new JdbcCursorItemReaderBuilder<CustomerNew>()
				.dataSource(this.dataSource)
				.name("creditReader")
				.sql("select id,firstName, lastName from Customer_new")
				.rowMapper(new CustomerRowMapper())
				.build();

	}
	
	@Bean
	@StepScope
	public CustomerProcessor processor() {
	    return new CustomerProcessor();
	}
	
	@Bean
	@StepScope
	public JdbcBatchItemWriter<CustomerNew> itemWriter(){
		JdbcBatchItemWriter<CustomerNew> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(this.dataSource);
		writer.setSql("INSERT INTO Customer_Updated VALUES (:id, :firstName, :lastName)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.afterPropertiesSet();
		
		return writer;
	}
	
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<CustomerNew, CustomerNew> chunk(1)
				.reader(itemReader())
				.processor(processor())
				.writer(itemWriter())
				.build();
	}
	
	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
				.<Customer, Customer> chunk(1000)
				.reader(customerItemReader())
				.writer(customerItemWriter())
				.build();
	}
	
	
	@Bean
	@StepScope
	public ManagerApprovalAppliactionProcessor ManagerProcessor() {
	    return new ManagerApprovalAppliactionProcessor();
	}
	
	@Bean
	@StepScope
	public ManagerApprovalAppliactionReader ManagerReader() {
	    return new ManagerApprovalAppliactionReader();
	}
	
	@Bean
	@StepScope
	public ManagerApprovalApplicationWriter ManagerWriter() {
	    return new ManagerApprovalApplicationWriter();
	}
	

	
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
				.<CustomerNew, CustomerNew> chunk(1)
				.reader(ManagerReader())
				.processor(ManagerProcessor())
				.writer(ManagerWriter())
				.build();
	}
	
	public JobParametersIncrementer jobParametersIncrementer() {
	    return new SampleIncrementer();
	}

	public JobBuilder getJobBuilder(String jobName) {
	    return jobBuilderFactory.get(jobName)
	            .incrementer(jobParametersIncrementer());
	}
	
	
	@Bean
	public Job job() {
		String jobane= "job"+System.currentTimeMillis();
		return getJobBuilder(jobane)
				.incrementer(new RunIdIncrementer())
				.start(step3())
				.build();
	}	
}
