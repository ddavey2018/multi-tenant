package com.ddavey.cmdb.identityprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentityProviderServiceImpl implements IdentityProviderService {

	@Autowired
	private IdentityProviderRepository identityProviderRepository;

	@Override
	public IdentityProviderDTO save(IdentityProviderDTO identityProvider) {
		identityProviderRepository.save(new IdentityProvider(identityProvider.getId(), identityProvider.getMetadata()));
		return identityProvider;
	}

}
