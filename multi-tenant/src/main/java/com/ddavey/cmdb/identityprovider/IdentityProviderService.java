package com.ddavey.cmdb.identityprovider;

import org.springframework.stereotype.Service;

@Service
public interface IdentityProviderService {
	// public IdentityProviderDTO getIdentityProvider(String id);

	public IdentityProviderDTO save(IdentityProviderDTO identityProvider);
}
