package com.billy5804.iotnoisedetectionbackend.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

public class User {

	private String id;
	private String displayName;

	public User(String id) {
		UserRecord userRecord = null;
		try {
			userRecord = FirebaseAuth.getInstance().getUser(id);
		} catch (FirebaseAuthException e) {
			e.printStackTrace();
		}
		this.id = userRecord.getUid();
		this.displayName = userRecord.getDisplayName();
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}
}