package com.example.demo.service;

import com.example.demo.controller.ws.Message;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public interface WalletService {

	/**
	 * @return an action that can cancel the callback.
	 */
	Callable<Void> subscriberForStateStream(Consumer<Message<Float>> subscriber);

	void withdraw(Message<Message.Trade> trade);

	void adjust(Message<Message.Trade> trade);

	void rollback(Message<Message.Trade> trade);
}
