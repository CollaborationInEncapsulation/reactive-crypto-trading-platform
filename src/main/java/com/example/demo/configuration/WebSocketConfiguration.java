package com.example.demo.configuration;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.controller.ws.CryptoChannel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

@Configuration
public class WebSocketConfiguration {

	@Bean
	public HandlerAdapter webSocketHandlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

	@Bean
	public HandlerMapping webSocketHandler(CryptoChannel channel) {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		Map<String, WebSocketHandler> urlMap = new HashMap<>();

		urlMap.put("/stream", channel);

		mapping.setUrlMap(urlMap);
		mapping.setOrder(0);

		return mapping;
	}
}