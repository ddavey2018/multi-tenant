package com.ddavey.customers;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(basePackages = "com.ddavey")
public class CustomerMultiTenentConnectionProvider extends AbstractMultiTenantConnectionProvider {

	private static final long serialVersionUID = 4484071460974465834L;
	@Autowired
	CustomerService customerService;

	public CustomerMultiTenentConnectionProvider() {
		initConnectionProvider();
	}

	@Override
	protected ConnectionProvider getAnyConnectionProvider() {
		return null;
	}

	@Override
	protected ConnectionProvider selectConnectionProvider(String tennantId) {
		// TODO Auto-generated method stub
		return null;
	}

	private void initConnectionProvider() {

	}

}
