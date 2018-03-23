package com.ddavey.customers;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.jboss.logging.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages =
{ "com.ddavey" }, excludeFilters =
{ @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerService.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerServiceImpl.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerRepository.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerEntity.class) })
@EnableJpaRepositories(basePackages =
{ "com.ddavey" },
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager",
        excludeFilters =
        { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerRepository.class),

        })

@PropertySource("classpath:application.properties")
@Order(2)
public class MultiTenantConfiguration
{
    private final Logger logger = Logger.getLogger(this.getClass());
    @Resource
    private Environment env;

    @Bean
    public JpaVendorAdapter jpaVendorAdapter()
    {
        return new HibernateJpaVendorAdapter();
    }

    @Bean("tenantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory(
            MultiTenantConnectionProvider connectionProvider, CurrentTenantIdentifierResolver tenantResolver)
    {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan(System.getProperty("entityPackageScan", "com.ddavey"));
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(additionalProperties(connectionProvider, tenantResolver));
        em.setPersistenceUnitName("tenants");
        em.afterPropertiesSet();
        return em;
    }

    @Bean(name = "tenantTransactionManager")
    public PlatformTransactionManager customerTransactionManager(EntityManagerFactory emf)
    {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    Map<String, Object> additionalProperties(MultiTenantConnectionProvider connectionProvider,
            CurrentTenantIdentifierResolver tenantResolver)

    {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.DIALECT,
                env.getProperty("spring.jpa.properties.hibernate.dialect"));
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);

        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        properties.put(org.hibernate.cfg.Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        return properties;
    }
}
