package com.example.demo.service.bitmex;

import java.net.URI;
import java.time.Duration;

import com.example.demo.service.CryptoService;
import com.example.demo.controller.ws.Message;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

@Service
public class BitmexCryptoService implements CryptoService {

	private final DirectProcessor<Message<?>> stream = DirectProcessor.create();

	public BitmexCryptoService() {
		new ReactorNettyWebSocketClient()
				.execute(
						URI.create("wss://www.bitmex.com/realtime?subscribe=instrument:XBTUSD,trade:XBTUSD"),
						new BitmexWebSocketHandler(stream.sink())
				)
				.retryWhen(e -> e.zipWith(Flux.range(0,  Integer.MAX_VALUE))
				                 .delayElements(Duration.ofMillis(200)))
				.subscribe();
	}

	@Override
	public Flux<Message<?>> stream() {
		return stream;
	}
}
