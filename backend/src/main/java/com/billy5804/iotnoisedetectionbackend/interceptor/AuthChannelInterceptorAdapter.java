package com.billy5804.iotnoisedetectionbackend.interceptor;

import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.billy5804.iotnoisedetectionbackend.model.AuthUser;
import com.billy5804.iotnoisedetectionbackend.model.FirebaseAuthenticationToken;
import com.billy5804.iotnoisedetectionbackend.model.SiteUserPK;
import com.billy5804.iotnoisedetectionbackend.provider.FirebaseIdTokenAuthenticationProvider;
import com.billy5804.iotnoisedetectionbackend.repository.SiteUserRepository;

@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

	SiteUserRepository siteUserRepository;

	public AuthChannelInterceptorAdapter(SiteUserRepository siteUserRepository) {
		this.siteUserRepository = siteUserRepository;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			Authentication user = new FirebaseIdTokenAuthenticationProvider()
					.authenticate(new FirebaseAuthenticationToken(accessor.getFirstNativeHeader("authorization")));
			accessor.setUser(user);
		} else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
			String[] destinationParts = accessor.getDestination().startsWith("/")
					? accessor.getDestination().replaceFirst("/", "").split("/")
					: accessor.getDestination().split("/");
			validateSubscription((AuthUser) accessor.getUser(), destinationParts);
		}
		return message;
	}

	private void validateSubscription(AuthUser authUser, String[] topicDestinationParts) {
		if (!(topicDestinationParts.length >= 3 && topicDestinationParts[0].equals("message"))) {
			throw new IllegalArgumentException("Invalid topic destination");
		}
		switch (topicDestinationParts[1]) {
		case "site-device":
			final UUID siteId = UUID.fromString(topicDestinationParts[2]);
			final boolean isAuthorised = siteUserRepository
					.existsByIdAndAuthorised(new SiteUserPK(siteId, authUser.getId()));
			if (!isAuthorised) {
				throw new IllegalArgumentException("No permission for this topic or invalid destination");
			}
			break;
		default:
			throw new IllegalArgumentException("Invalid topic destination");
		}
	}
}
