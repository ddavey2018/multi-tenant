package com.ddavey.cmdb.identityprovider;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface IdentityProviderRepository extends CrudRepository<IdentityProvider, String> {

}
