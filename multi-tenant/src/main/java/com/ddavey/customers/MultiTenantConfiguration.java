package com.ddavey.customers;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.jboss.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.ddavey", excludeFilters =
{ @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerRepository.class) })
@ComponentScan(basePackages =
{ "com.ddavey" }, excludeFilters =
{ @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value =
        { CustomerServiceImpl.class, CustomerRepository.class }) })
@Order(2)
public class MultiTenantConfiguration
{
    private final Logger logger = Logger.getLogger(this.getClass());
    @Resource
    private Environment env;

    @Bean("entityManagerFactory2")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        String appName = env.getProperty("spring.application.name", "");

        String packagesToScan = "com.ddavey" + (appName.length() > 0 ? "." + appName : "");
        if (appName.length() == 0)
        {
            logger.warnv("No application name set, falling back to package scan %s for entityManagerFactory2",
                    new Object[]
                    { packagesToScan });
        }

        em.setPackagesToScan(packagesToScan);
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setDataSource(datasource());
        em.setJpaPropertyMap(additionalProperties());
        em.afterPropertiesSet();
        return em;
    }

    public DriverManagerDataSource datasource()
    {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        datasource.setUrl(env.getRequiredProperty("spring.datasource.url"));
        datasource.setUsername(env.getRequiredProperty("spring.datasource.username"));
        datasource.setPassword(env.getProperty("spring.datasource.password"));
        return datasource;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager customerTransactionManager(EntityManagerFactory emf)
    {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation()
    {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public CustomerMultiTenentConnectionProvider customerConnectionProvider()
    {
        // return new CustomerMultiTenentConnectionProvider(datasource(),
        // resolver, env);
        return new CustomerMultiTenentConnectionProvider();
    }

    @Bean
    public MultiTenantIdentifierResolver tenantResolver()
    {
        return new MultiTenantIdentifierResolver();
    }

    Map<String, Object> additionalProperties()

    {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.DIALECT,
                env.getProperty("spring.jpa.properties.hibernate.dialect"));
        MultiTenantIdentifierResolver resolver = tenantResolver();
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, customerConnectionProvider());

        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        return properties;
    }
}
