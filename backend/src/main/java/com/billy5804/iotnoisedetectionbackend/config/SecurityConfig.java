package com.billy5804.iotnoisedetectionbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.billy5804.iotnoisedetectionbackend.filter.FirebaseIdTokenFilter;
import com.billy5804.iotnoisedetectionbackend.provider.FirebaseIdTokenAuthenticationProvider;

@EnableWebSecurity
@Component
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final FirebaseIdTokenAuthenticationProvider authenticationProvider;
	
	@Autowired
	public SecurityConfig(FirebaseIdTokenAuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new FirebaseIdTokenFilter(), BasicAuthenticationFilter.class)
			.authorizeRequests()
			.antMatchers("/api/v1/*")
			.authenticated();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
	}

}
