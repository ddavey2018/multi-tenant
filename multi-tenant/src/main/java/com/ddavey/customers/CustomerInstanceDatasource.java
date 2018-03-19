package com.ddavey.customers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class CustomerInstanceDatasource extends DriverManagerDataSource {

	boolean started = false;
	@Autowired
	private CustomerService customerService;

	@Override
	protected Connection getConnectionFromDriver(Properties props) throws SQLException {
		String url = getUrl();
		/*
		 * try { ServletRequestAttributes reqAttr = (ServletRequestAttributes)
		 * RequestContextHolder .currentRequestAttributes(); Integer customerNo =
		 * (Integer)
		 * reqAttr.getAttribute(CustomerSessionConstants.ATTRIBUTE_CUSTOMER_NO, -1); if
		 * (customerNo != null) { url = getCustomerConnectionUrl(customerNo); } } catch
		 * (IllegalStateException e) { Integer[] ids = customerService.getCustomerIds();
		 * for (Integer id : ids) { url = getCustomerConnectionUrl(id);
		 * logger.info("Creating new JDBC DriverManager Connection to [" + url + "]");
		 * getConnectionFromDriverManager(getCustomerConnectionUrl(id), props); } url =
		 * getUrl(); }
		 */
		// if (logger.isDebugEnabled()) {
		logger.info("Creating new JDBC DriverManager Connection to [" + url + "]");
		// }
		return getConnectionFromDriverManager(url, props);
	}

	private String getCustomerConnectionUrl(Integer customerNo) {
		String url = getUrl();
		return String.join(String.format("_%d?", customerNo), url.split("\\?"));
	}

	public CustomerInstanceDatasource() {
		System.out.println();
	}
}
