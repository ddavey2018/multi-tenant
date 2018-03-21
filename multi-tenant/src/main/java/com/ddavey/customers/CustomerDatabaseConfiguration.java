package com.ddavey.customers;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableJpaRepositories("com.ddavey.customers")
@ComponentScan(basePackages =
{ "com.ddavey.customers" })
@Order(1)
public class CustomerDatabaseConfiguration
{

    @Resource
    private Environment env;

    @Primary
    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan(new String[]
        { "com.ddavey.customers" });
        em.setDataSource(datasource());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(additionalProperties());
        em.afterPropertiesSet();
        return em;
    }

    @Bean
    @Primary
    public DriverManagerDataSource datasource()
    {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        datasource.setUrl(env.getRequiredProperty("spring.datasource.url"));
        datasource.setUsername(env.getRequiredProperty("spring.datasource.username"));
        datasource.setPassword(env.getProperty("spring.datasource.password"));
        return datasource;
    }

    @Bean
    @Primary
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

    /*
     * @Bean public CustomerMultiTenentConnectionProvider
     * customerConnectionProvider(MultiTenantIdentifierResolver resolver) {
     * return new CustomerMultiTenentConnectionProvider(datasource(), resolver,
     * env); }
     * 
     * @Bean public MultiTenantIdentifierResolver tenantResolver() { return new
     * MultiTenantIdentifierResolver(); }
     */

    Map<String, Object> additionalProperties()

    {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.DIALECT,
                env.getProperty("spring.jpa.properties.hibernate.dialect"));
        /*
         * MultiTenantIdentifierResolver resolver = tenantResolver();
         * properties.put(org.hibernate.cfg.Environment.
         * MULTI_TENANT_CONNECTION_PROVIDER,
         * customerConnectionProvider(resolver));
         * 
         * properties.put(org.hibernate.cfg.Environment.
         * MULTI_TENANT_IDENTIFIER_RESOLVER, resolver);
         * properties.put(org.hibernate.cfg.Environment.MULTI_TENANT,
         * MultiTenancyStrategy.DATABASE);
         */
        return properties;
    }
}
