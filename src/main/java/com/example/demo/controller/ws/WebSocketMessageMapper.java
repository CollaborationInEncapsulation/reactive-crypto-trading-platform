package com.example.demo.controller.ws;

import java.util.Collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;

@Service
public class WebSocketMessageMapper {

	private final Jackson2JsonEncoder encoder;
	private final Jackson2JsonDecoder decoder;

	public WebSocketMessageMapper(ObjectMapper mapper) {
		encoder = new Jackson2JsonEncoder(mapper);
		decoder = new Jackson2JsonDecoder(mapper);
	}

	public Flux<DataBuffer> encode(Flux<?> outbound, DataBufferFactory dataBufferFactory) {
		return outbound
				.flatMap(i -> encoder.encode(
						Mono.just(i),
						dataBufferFactory,
						ResolvableType.forType(Object.class),
						MediaType.APPLICATION_JSON,
						Collections.emptyMap()
				));

	}

	@SuppressWarnings("unchecked")
	public Flux<Message<Message.Trade>> decode(Flux<DataBuffer> inbound) {
		return inbound.flatMap(p -> decoder.decode(
                      		Mono.just(p),
		                    ResolvableType.forType(new ParameterizedTypeReference<Message<Message.Trade>>() {}),
		                    MediaType.APPLICATION_JSON,
		                    Collections.emptyMap()
                      ))
                      .map(o -> (Message<Message.Trade>) o);
	}
}