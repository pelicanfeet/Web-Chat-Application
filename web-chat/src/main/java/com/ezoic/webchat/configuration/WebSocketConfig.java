package com.ezoic.webchat.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// Enable the WebSocket server
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/* Register a WebSocket endpoint that the clients will use to connect to
	** the WebSocket server.
	** The call to withSockJS() enables options for browsers that do not 
	** support WebSocket.
	*/
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").withSockJS();
	}
	
	/* Configures a message broker that routes messages from one client to
	** another.
	** More specifically, this method specifies that messages whose destination
	** starts with "/app" should be routed to message handling methods and
	** messages whose destination starts with "/topic" should be routed to the
	** message broker.
	*/
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}
}