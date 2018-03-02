package com.example.demo.service.bitfinex;

import java.net.URI;
import java.time.Duration;
import java.util.logging.Level;

import com.example.demo.controller.ws.Message;
import com.example.demo.service.CryptoService;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

@Service
public class BitfinexCryptoService implements CryptoService {

	private final DirectProcessor<Message<?>> stream = DirectProcessor.create();

	public BitfinexCryptoService() {
		new ReactorNettyWebSocketClient()
				.execute(
						URI.create("wss://api.bitfinex.com/ws/2"),
						new BitfinexWebSocketHandler(stream.sink())
				)
				.log("Connected TO Bitfinex", Level.INFO, SignalType.ON_SUBSCRIBE)
				.retryWhen(e -> e.zipWith(Flux.range(0,  Integer.MAX_VALUE))
				                 .delayElements(Duration.ofMillis(200)))
				.subscribe();
	}

	@Override
	public Flux<Message<?>> stream() {
		return stream;
	}
}
