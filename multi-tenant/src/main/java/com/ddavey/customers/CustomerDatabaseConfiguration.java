package com.ddavey.customers;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.jboss.logging.Logger;
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
@EnableJpaRepositories(basePackages =
{ "com.ddavey.customers" },
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
@ComponentScan(basePackages =
{ "com.ddavey.customers" })
@Order(1)
public class CustomerDatabaseConfiguration
{
    private Logger logger = Logger.getLogger(CustomerDatabaseConfiguration.class);
    @Resource
    private Environment env;

    @Primary
    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean customerEntityManagerFactory()
    {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPackagesToScan(new String[]
        { CustomerEntity.class.getPackage().getName() });
        em.setDataSource(datasource());
        em.setPersistenceUnitName("customers");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setJpaPropertyMap(additionalProperties());
        em.afterPropertiesSet();
        setApplicationEntityPackageScanVariable();
        return em;
    }

    private void setApplicationEntityPackageScanVariable()
    {
        String appName = env.getProperty("spring.application.name", "");
        String packagesToScan = "com.ddavey" + (appName.length() > 0 ? "." + appName : "");
        if (appName.length() == 0)
        {
            logger.warnv("No application name set, falling back to package scan %s for tenantEntityManagerFactory",
                    new Object[]
                    { packagesToScan });
        }
        System.setProperty("entityPackageScan", packagesToScan);
    }

    @Bean("customerDatasource")
    public DriverManagerDataSource datasource()
    {
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName(env.getRequiredProperty("spring.datasource.driver-class-name"));
        datasource.setUrl(env.getRequiredProperty("spring.datasource.url"));
        datasource.setUsername(env.getRequiredProperty("spring.datasource.username"));
        datasource.setPassword(env.getProperty("spring.datasource.password"));
        return datasource;
    }

    @Bean("transactionManager")
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

    Map<String, Object> additionalProperties()

    {
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, true);
        properties.put(org.hibernate.cfg.Environment.DIALECT,
                env.getProperty("spring.jpa.properties.hibernate.dialect"));
        return properties;
    }
}
