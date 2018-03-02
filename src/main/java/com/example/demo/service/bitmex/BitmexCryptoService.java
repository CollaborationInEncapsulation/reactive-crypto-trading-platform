package com.example.demo.service.bitmex;

import java.net.URI;
import java.time.Duration;
import java.util.logging.Level;

import com.example.demo.controller.ws.Message;
import com.example.demo.service.CryptoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.SignalType;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

@Service
public class BitmexCryptoService implements CryptoService {

	private final Flux<Message<?>> stream = Flux.create(BitmexCryptoService::connect)
	                                            .publish()
	                                            .autoConnect(0);

	@Override
	public Flux<Message<?>> stream() {
		return stream;
	}

	private static void connect(FluxSink<Message<?>> sink) {
		new ReactorNettyWebSocketClient()
				.execute(
						URI.create("wss://www.bitmex.com/realtime?subscribe=instrument:XBTUSD,trade:XBTUSD"),
						new BitmexWebSocketHandler(sink)
				)
				.log("Connected TO Bitmex", Level.INFO, SignalType.ON_SUBSCRIBE)
				.retryWhen(e -> e.zipWith(Flux.range(0,  Integer.MAX_VALUE))
				                 .delayElements(Duration.ofMillis(200)))
				.subscribe();
	}
}
