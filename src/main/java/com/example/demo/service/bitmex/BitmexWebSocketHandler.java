package com.example.demo.service.bitmex;

import com.example.demo.controller.ws.Message;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

@RequiredArgsConstructor
public class BitmexWebSocketHandler implements WebSocketHandler {

	private final FluxSink<Message<?>> sink;

	@Override
	public Mono<Void> handle(WebSocketSession s) {
		return s.receive()
		        .skip(6)
		        .map(WebSocketMessage::getPayloadAsText)
		        .publishOn(Schedulers.parallel())
		        .flatMapIterable(BitmexMessageMapper::bitmexToMessage)
		        .doOnNext(sink::next)
		        .then();
	}
}
