package com.ddavey.customers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	private final Map<String, Integer> map = new HashMap<String, Integer>();

	public CustomerServiceImpl() {
		map.put("customer2", 2);
	}

	public Integer getIdForUrl(String url) {
		CustomerEntity customer = customerRepository.findByUrl(url);
		return customer.getId();
	}

	public Integer[] getCustomerIds() {
		List<CustomerEntity> customers = customerRepository.findAll();
		List<Integer> ids = new ArrayList<Integer>();
		int index = 0;
		customers.stream().forEach(customer -> ids.add(customer.getId()));
		return ids.toArray(new Integer[ids.size()]);
	}

}
