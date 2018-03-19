package com.ddavey.customers;

import java.util.List;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@org.springframework.stereotype.Repository
public interface CustomerRepository extends Repository<CustomerEntity, Integer>{
	public CustomerEntity findByUrl(String url);
	
	public void save(CustomerEntity customer);
	
	public List<CustomerEntity> findAll();
}
