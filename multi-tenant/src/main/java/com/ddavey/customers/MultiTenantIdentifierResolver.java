package com.ddavey.customers;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class MultiTenantIdentifierResolver implements CurrentTenantIdentifierResolver
{

    @Override
    public String resolveCurrentTenantIdentifier()
    {
        return "1";
    }

    @Override
    public boolean validateExistingCurrentSessions()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
