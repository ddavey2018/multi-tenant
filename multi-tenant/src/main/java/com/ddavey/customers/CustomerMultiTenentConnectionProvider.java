package com.ddavey.customers;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@PropertySource("classpath:application.properties")
public class CustomerMultiTenentConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

	private static final long serialVersionUID = 4484071460974465834L;
	private DriverManagerDataSource defaultDatasource;
	private Map<Integer, DataSource> customerDatasources = new HashMap<Integer, DataSource>();

	private MultiTenantIdentifierResolver resolver;

	private Environment env;

	public CustomerMultiTenentConnectionProvider(DriverManagerDataSource datasource,
			MultiTenantIdentifierResolver resolver, Environment env) {
		this.resolver = resolver;
		this.defaultDatasource = datasource;
		this.env = env;
		init();
	};

	@Override
	protected DataSource selectAnyDataSource() {
		return defaultDatasource;
	}

	@Override
	protected DataSource selectDataSource(String tenantId) {
		return customerDatasources.get(Integer.valueOf(tenantId));
	}

	private void init() {
		for (int i = 1; i <= 5; i++) {
			addTenant(i);
			System.out.println(String.format("Connection to cutomer db %d", i));
		}
	}

	private void addTenant(Integer tenantId) {
		DriverManagerDataSource copyDatasource = (DriverManagerDataSource) this.defaultDatasource;
		String url = String.join(String.format("_%d?", tenantId), copyDatasource.getUrl().split("\\?"));
		DriverManagerDataSource tenantDatasource = new DriverManagerDataSource(url, copyDatasource.getUsername(),
				copyDatasource.getPassword());
		customerDatasources.put(tenantId, tenantDatasource);
		entityManagerFactory(tenantDatasource, tenantId);
	}

	private void entityManagerFactory(DriverManagerDataSource tenantDatasource, Integer tenantId) {
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		emfBean.setDataSource(tenantDatasource);
		emfBean.setPackagesToScan("com.ddavey");
		emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emfBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		Map<String, Object> properties = new HashMap<>();
		properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
		properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, this);
		properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
		properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
		properties.put("hibernate.dialect",
				env.getProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect"));
		properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql", "true"));
		properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql", "true"));
		properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto", "update"));
		emfBean.setJpaPropertyMap(properties);
		emfBean.setPersistenceUnitName(String.format("tenant_%d", tenantId));
		emfBean.afterPropertiesSet();
	}

}
