package com.billy5804.iotnoisedetectionbackend.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken{
	
	public final String idToken;

	public FirebaseAuthenticationToken(String idToken) {
		super(null);
		this.idToken = idToken;
	}
	
	public String getIdToken() {
		return idToken;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

}
