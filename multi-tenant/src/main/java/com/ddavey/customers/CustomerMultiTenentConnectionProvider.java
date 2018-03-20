package com.ddavey.customers;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;

public class CustomerMultiTenentConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl
{

    private static final long serialVersionUID = 4484071460974465834L;
    private DataSource defaultDatasource;

    public CustomerMultiTenentConnectionProvider(DataSource datasource)
    {
        this.defaultDatasource = datasource;
        init();
    };

    public CustomerMultiTenentConnectionProvider()
    {
        init();
    }

    @Override
    protected DataSource selectAnyDataSource()
    {
        // TODO Auto-generated method stub
        return defaultDatasource;
    }

    @Override
    protected DataSource selectDataSource(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private void init()
    {
    }

}
