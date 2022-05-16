package com.billy5804.iotnoisedetectionbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
@Order(3)
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
	@Override
	protected void configureInbound(final MessageSecurityMetadataSourceRegistry messages) {
		messages.anyMessage().authenticated();
	}

	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}
