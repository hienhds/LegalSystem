package com.example.backend.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker with "/topic" prefix
        config.enableSimpleBroker("/topic");
        // Set application destination prefix for messages bound for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register "/chat" endpoint for WebSocket connections
        // Allow all origins (for development - restrict in production)
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:5173", "http://localhost:3000")
                .withSockJS();
    }
}
