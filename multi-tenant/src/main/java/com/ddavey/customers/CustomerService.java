package com.ddavey.customers;

import org.springframework.stereotype.Service;

@Service
public interface CustomerService {
	public Integer getIdForUrl(String url);

	public Integer[] getCustomerIds();
}
