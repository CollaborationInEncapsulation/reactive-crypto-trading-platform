package com.example.demo.service;

import com.example.demo.controller.ws.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletService {

	Flux<Message<Float>> stateStream();

	Mono<Void> withdraw(Message<Message.Trade> trade);

	Mono<Void> adjust(Message<Message.Trade> trade);

	Mono<Void> rollback(Message<Message.Trade> trade);
}
