package com.ddavey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*
 * @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
 * DataSourceTransactionManagerAutoConfiguration.class,
 * HibernateJpaAutoConfiguration.class })
 */
public class MultiTenantApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(MultiTenantApplication.class, args);
    }
}
