package com.ddavey.cmdb.identityprovider;

public class IdentityProviderDTO {
	private String id;

	private String metadata;

	public IdentityProviderDTO(String id, String metadata) {
		super();
		this.id = id;
		this.metadata = metadata;
	}

	public IdentityProviderDTO() {
		super();
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
