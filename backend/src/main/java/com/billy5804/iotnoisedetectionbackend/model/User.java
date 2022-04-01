package com.billy5804.iotnoisedetectionbackend.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

public class User {

	@JsonIgnore
	protected UserRecord userRecord;
	@JsonIgnore
	private final String passedId;

	public User() {
		this.passedId = null;
	}
	
	public User(String id) {
		passedId = id;
		try {
			userRecord = FirebaseAuth.getInstance().getUser(id);
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
		}
	}

	@JsonGetter
	public String getId() {
		return userRecord != null ? userRecord.getUid() : passedId;
	}

	@JsonGetter
	public String getDisplayName() {
		return userRecord != null ? userRecord.getDisplayName() : "Unknown User";
	}
}