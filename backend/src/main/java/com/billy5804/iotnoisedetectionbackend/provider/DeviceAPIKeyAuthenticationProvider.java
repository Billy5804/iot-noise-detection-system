package com.billy5804.iotnoisedetectionbackend.provider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.billy5804.iotnoisedetectionbackend.model.DeviceAuthenticationToken;

@Component
public class DeviceAPIKeyAuthenticationProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(DeviceAPIKeyAuthenticationProvider.class);

	private static final String API_KEY = "SuperSecretKey";

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String authHeader = (String) authentication.getCredentials();
		byte[] body = (byte[]) authentication.getPrincipal();
		final byte[] apiKey = API_KEY.getBytes();
		final byte[] expectedRawAuthBytes = new byte[body.length + apiKey.length];
		System.arraycopy(body, 0, expectedRawAuthBytes, 0, body.length);
		System.arraycopy(apiKey, 0, expectedRawAuthBytes, body.length, apiKey.length);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		final String expectedAuth = HexFormat.of().formatHex(digest.digest(expectedRawAuthBytes));

		if (expectedAuth.equalsIgnoreCase(authHeader)) {
			authentication.setAuthenticated(true);
		} else {
			throw new SecurityException("Authentication failed");
		}

		logger.info("Requesting device auth: {}", authHeader);
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.isAssignableFrom(DeviceAuthenticationToken.class);
	}

}
