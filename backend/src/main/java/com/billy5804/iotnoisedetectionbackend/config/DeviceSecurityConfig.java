package com.billy5804.iotnoisedetectionbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.billy5804.iotnoisedetectionbackend.filter.DeviceAPIKeyFilter;

@Configuration
@Order(2)
public class DeviceSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new DeviceAPIKeyFilter(), BasicAuthenticationFilter.class).csrf().disable()
				.authorizeRequests().antMatchers("/api/v1/devices").authenticated();
	}
}
