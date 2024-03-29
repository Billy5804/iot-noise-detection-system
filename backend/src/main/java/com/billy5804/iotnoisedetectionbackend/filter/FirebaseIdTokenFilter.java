package com.billy5804.iotnoisedetectionbackend.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.billy5804.iotnoisedetectionbackend.model.FirebaseAuthenticationToken;

public class FirebaseIdTokenFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authorization = request.getHeader("Authorization");
		try {
			SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthenticationToken(authorization));
			filterChain.doFilter(request, response);
		} catch (SecurityException e) {
			response.reset();
			response.addHeader("WWW-authenticate", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
