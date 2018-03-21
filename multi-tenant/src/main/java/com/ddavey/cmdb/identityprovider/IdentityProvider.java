package com.ddavey.cmdb.identityprovider;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
class IdentityProvider {
	@Id
	private String id;

	private String metadata;

	IdentityProvider(String id, String metadata) {
		super();
		this.id = id;
		this.metadata = metadata;
	}

	IdentityProvider() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

}
