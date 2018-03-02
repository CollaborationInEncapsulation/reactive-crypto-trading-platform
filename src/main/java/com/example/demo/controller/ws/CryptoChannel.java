package com.example.demo.controller.ws;

import java.util.List;

import com.example.demo.service.CryptoService;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CryptoChannel implements WebSocketHandler {

	private final WebSocketMessageMapper mapper;
	private final List<CryptoService>    cryptoServices;
	private final WalletService          walletService;

	@Override
	public Mono<Void> handle(WebSocketSession session) {
		return session.receive()
		              .map(WebSocketMessage::retain)
		              .map(WebSocketMessage::getPayload)
		              .publishOn(Schedulers.parallel())
		              .transform(mapper::decode)
		              .transform(this::doHandle)
		              .onBackpressureBuffer()
		              .transform(m -> mapper.encode(m, session.bufferFactory()))
		              .map(db -> new WebSocketMessage(WebSocketMessage.Type.TEXT, db))
		              .as(session::send);
	}

	private Flux<?> doHandle(Flux<Message<Message.Trade>> inbound) {
		return Flux.merge(
				walletService.stateStream(),
				Flux.fromIterable(cryptoServices)
				    .flatMap(CryptoService::stream)
		);
	}
}
