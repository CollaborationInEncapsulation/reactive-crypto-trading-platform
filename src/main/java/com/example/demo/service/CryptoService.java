package com.example.demo.service;

import com.example.demo.controller.ws.Message;
import reactor.core.publisher.Flux;

public interface CryptoService {

	Flux<Message<?>> stream();
}

