package com.example.demo.service;

import java.util.List;

public interface CryptoService {

	List<Message<?>> retrieve(Long timestamp);

	default void store(Message<Message.Trade> tradeOffer) { }
}
