package com.example.demo.service;

import com.example.demo.controller.ws.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CryptoService {

	Flux<Message<?>> stream();

	default Mono<Void> trade(Flux<Message<Message.Trade>> trades, WalletService walletService) {
		return Mono.empty();
	}
}

