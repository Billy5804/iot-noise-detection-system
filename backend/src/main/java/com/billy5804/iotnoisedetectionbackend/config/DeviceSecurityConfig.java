package com.billy5804.iotnoisedetectionbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.billy5804.iotnoisedetectionbackend.filter.DeviceAPIKeyFilter;
import com.billy5804.iotnoisedetectionbackend.provider.DeviceAPIKeyAuthenticationProvider;

// Device only API endpoint security config for checking the incoming request is an authenticated device.
@EnableWebSecurity
@Component
@Order(2)
public class DeviceSecurityConfig extends WebSecurityConfigurerAdapter {

	private final DeviceAPIKeyAuthenticationProvider authenticationProvider;

	@Autowired
	public DeviceSecurityConfig(DeviceAPIKeyAuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new DeviceAPIKeyFilter(), BasicAuthenticationFilter.class).csrf().disable()
				.authorizeRequests().antMatchers("/api/v1/devices").authenticated();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider);
	}
}
