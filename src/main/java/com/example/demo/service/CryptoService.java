package com.example.demo.service;

import com.example.demo.controller.ws.Message;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public interface CryptoService {

	Callable<Void> subscribe(Consumer<Message<?>> subscriber);

	default void trade(Message<Message.Trade> trade) {
	}
}

