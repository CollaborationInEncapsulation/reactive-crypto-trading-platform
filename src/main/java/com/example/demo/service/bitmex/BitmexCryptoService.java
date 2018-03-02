package com.example.demo.service.bitmex;

import java.net.URI;
import java.util.logging.Level;

import com.example.demo.controller.ws.Message;
import com.example.demo.service.CryptoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

@Service
public class BitmexCryptoService implements CryptoService {

	@Override
	public Flux<Message<?>> stream() {
		return Flux
				.create(sink ->
					new ReactorNettyWebSocketClient()
						.execute(
							URI.create("wss://www.bitmex.com/realtime?subscribe=instrument:XBTUSD,trade:XBTUSD"),
							new WebSocketHandler() {
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
						)
						.log("Connected TO Bitmex", Level.INFO, SignalType.ON_SUBSCRIBE)
						.subscribe()
				);
	}
}
