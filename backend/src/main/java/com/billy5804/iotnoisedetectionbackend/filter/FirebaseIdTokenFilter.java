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
		final String idToken = authorization != null ? authorization.replace("Bearer ", "") : "";
		SecurityContextHolder.getContext().setAuthentication(new FirebaseAuthenticationToken(idToken));
		filterChain.doFilter(request, response);		
	}

}
