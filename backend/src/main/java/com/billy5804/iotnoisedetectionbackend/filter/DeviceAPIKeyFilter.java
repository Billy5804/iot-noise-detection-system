package com.billy5804.iotnoisedetectionbackend.filter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class DeviceAPIKeyFilter extends OncePerRequestFilter {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		System.out.println("Device API");
		final String authorization = request.getHeader("Authorization");
		final byte[] body = request.getInputStream().readAllBytes();
		final byte[] apiKey = "SuperSecretKey".getBytes();
		final byte[] expectedAuthorizationBytes = new byte[body.length + apiKey.length];
		System.arraycopy(body, 0, expectedAuthorizationBytes, 0, body.length);
		System.arraycopy(apiKey, 0, expectedAuthorizationBytes, body.length, apiKey.length);
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		final String expectedAuthorization = bytesToHex(digest.digest(expectedAuthorizationBytes));
		
		if (expectedAuthorization.equals(authorization)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		response.reset();
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

}
