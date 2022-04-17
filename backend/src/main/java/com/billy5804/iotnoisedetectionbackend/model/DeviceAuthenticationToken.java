package com.billy5804.iotnoisedetectionbackend.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class DeviceAuthenticationToken extends AbstractAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8016698024890834202L;

	private final Object principal;

	private Object credentials;

	public DeviceAuthenticationToken(Object principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		this.credentials = null;
	}

}
