package com.example.demo.service.bitfinex;

import com.example.demo.controller.ws.Message;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

@RequiredArgsConstructor
public class BitfinexWebSocketHandler implements WebSocketHandler {
    private static final String TRADES_REQUEST = "{\"event\": \"subscribe\", \"channel\": \"trades\", \"pair\": \"BTCUSD\"}";
    private static final String PRICE_REQUEST  = "{\"event\": \"subscribe\", \"channel\": \"ticker\", \"pair\": \"BTCUSD\"}";

    private final FluxSink<Message<?>> sink;

	@Override
	public Mono<Void> handle(WebSocketSession s) {
		return Flux.just(TRADES_REQUEST, PRICE_REQUEST)
		           .map(s::textMessage)
		           .as(s::send)
		           .thenMany(s.receive())
		           .map(WebSocketMessage::getPayloadAsText)
		           .filter(payload -> payload.contains("[") && payload.contains("]"))
		           .publishOn(Schedulers.parallel())
		           .map(BitfinexMessageMapper::bitfinexToMessage)
		           .flatMap(Flux::fromArray)
		           .doOnNext(sink::next)
		           .then();
	}
}
