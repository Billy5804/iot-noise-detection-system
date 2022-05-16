package com.billy5804.iotnoisedetectionbackend.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.billy5804.iotnoisedetectionbackend.model.FirebaseAuthenticationToken;
import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

@Component
public class FirebaseIdTokenAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(FirebaseIdTokenAuthenticationProvider.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		FirebaseAuthenticationToken token = (FirebaseAuthenticationToken) authentication;
		try {
			FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token.getIdToken());
			String userId = firebaseToken.getUid();
			UserRecord userRecord = FirebaseAuth.getInstance().getUser(userId);
			logger.info("Requesting user, uid: {}", userRecord.getUid());
			final AuthUser authUser = new AuthUser(userRecord);
			if (!authUser.isAuthenticated()) {
				throw new SecurityException("Email not verified");
			}
			return authUser;
		} catch (FirebaseAuthException e) {
			if (e.getErrorCode().equals("id-token-revoked")) {
				throw new SecurityException("User token has been revoked");
			} else {
				throw new SecurityException("Auth failed");
			}
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(FirebaseAuthenticationToken.class);
	}

}
