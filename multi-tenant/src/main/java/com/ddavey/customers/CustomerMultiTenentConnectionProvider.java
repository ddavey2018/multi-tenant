package com.ddavey.customers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
public class CustomerMultiTenentConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl
{

    private static final long serialVersionUID = 4484071460974465834L;
    private DriverManagerDataSource defaultDatasource;
    private Map<Integer, DataSource> customerDatasources = new HashMap<Integer, DataSource>();

    @Resource
    private Environment env;

    @Autowired
    private CustomerService customerService;

    public CustomerMultiTenentConnectionProvider()
    {
    }

    @Override
    protected DataSource selectAnyDataSource()
    {
        if (this.defaultDatasource == null)
        {
            init();
        }
        return defaultDatasource;
    }

    @Override
    protected DataSource selectDataSource(String tenantId)
    {
        Integer iTenantId = Integer.valueOf(tenantId);
        if (!customerDatasources.containsKey(iTenantId))
        {
            customerDatasources.put(iTenantId, createTenantDatasource(iTenantId));
        }
        return customerDatasources.get(iTenantId);
    }

    private void init()
    {

        Integer[] customerIds = customerService.getCustomerIds();

        for (Integer customerId : customerIds)
        {
            addTenant(customerId.intValue());
            System.out.println(String.format("Connection to cutomer db %d", customerId.intValue()));
        }

        if (customerIds.length == 0)
        {
            constructDefaultDatasource();
        }
    }

    private void constructDefaultDatasource()
    {
        this.defaultDatasource = new DriverManagerDataSource(env.getRequiredProperty("spring.datasource.url"),
                env.getRequiredProperty("spring.datasource.username"),
                env.getRequiredProperty("spring.datasource.password"));
        this.defaultDatasource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
    }

    private DriverManagerDataSource createTenantDatasource(Integer tenantId)
    {
        String url = String.join(String.format("_%d?", tenantId),
                env.getRequiredProperty("spring.datasource.url").split("\\?"));
        DriverManagerDataSource tenantDatasource = new DriverManagerDataSource(url,
                env.getRequiredProperty("spring.datasource.username"),
                env.getRequiredProperty("spring.datasource.password"));
        tenantDatasource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        return tenantDatasource;
    }

    private void addTenant(Integer tenantId)
    {

        entityManagerFactory(createTenantDatasource(tenantId), tenantId);
    }

    private void entityManagerFactory(DriverManagerDataSource tenantDatasource, Integer tenantId)
    {
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(tenantDatasource);
        emfBean.setPackagesToScan("com.ddavey.entity");
        emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emfBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        Map<String, Object> properties = new HashMap<>();
        /*
         * properties.put(org.hibernate.cfg.Environment.MULTI_TENANT,
         * MultiTenancyStrategy.DATABASE);
         * properties.put(org.hibernate.cfg.Environment.
         * MULTI_TENANT_CONNECTION_PROVIDER, this);
         * properties.put(org.hibernate.cfg.Environment.
         * MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
         */
        properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
        properties.put(org.hibernate.cfg.Environment.DIALECT,
                env.getProperty(org.hibernate.cfg.Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect"));
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL,
                env.getProperty(org.hibernate.cfg.Environment.SHOW_SQL, "true"));
        properties.put(org.hibernate.cfg.Environment.FORMAT_SQL,
                env.getProperty(org.hibernate.cfg.Environment.FORMAT_SQL, "true"));
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO,
                env.getProperty(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update"));
        emfBean.setJpaPropertyMap(properties);
        emfBean.setPersistenceUnitName(String.format("tenant_%d", tenantId));
        emfBean.afterPropertiesSet();
    }

}
