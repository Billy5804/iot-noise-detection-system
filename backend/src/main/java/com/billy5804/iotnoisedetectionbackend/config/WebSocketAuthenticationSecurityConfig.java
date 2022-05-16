package com.billy5804.iotnoisedetectionbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.billy5804.iotnoisedetectionbackend.interceptor.AuthChannelInterceptorAdapter;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationSecurityConfig implements WebSocketMessageBrokerConfigurer {

	@Autowired
	SiteUserRepository siteUserRepository;

	@Override
	public void configureClientInboundChannel(final ChannelRegistration registration) {
		registration.interceptors(new AuthChannelInterceptorAdapter(siteUserRepository));
	}

}
