package com.webcheckers.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.webcheckers.api.messages.MessageHandler;
import com.webcheckers.api.service.MessageService;
import com.webcheckers.api.service.MessageServiceImpl;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
	
	@Bean
	public MessageService messageService() {
		return new MessageServiceImpl();
	}
	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new MessageHandler(messageService()), "/checkers").setAllowedOrigins("*").withSockJS();
	}
}
