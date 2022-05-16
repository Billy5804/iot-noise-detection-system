package com.billy5804.iotnoisedetectionbackend.model;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.auth.UserRecord;

public class AuthUser extends User implements Authentication {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4833866484002586294L;

	public AuthUser(UserRecord userRecord) {
		super.userRecord = userRecord;
	}

	@JsonIgnore
	@Override
	public String getName() {
		return userRecord.getUid();
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	@Override
	public boolean isAuthenticated() {
		return !userRecord.isDisabled() && userRecord.isEmailVerified();
	}

	@JsonIgnore
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
	}

}
