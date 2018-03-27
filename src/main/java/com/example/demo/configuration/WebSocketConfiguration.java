package com.example.demo.configuration;

import com.example.demo.controller.ws.CryptoChannel;
import com.example.demo.controller.ws.WebSocketMessageMapper;
import com.example.demo.service.CryptoService;
import com.example.demo.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

@Configuration
@EnableWebSocket
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WebSocketConfiguration implements WebSocketConfigurer {

	private final WebSocketMessageMapper mapper;
	private final List<CryptoService>cryptoServices;
	private final WalletService walletService;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(myHandler(), "/stream");
	}

	private WebSocketHandler myHandler() {
		return new CryptoChannel(mapper, cryptoServices, walletService);
	}
}