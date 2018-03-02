package com.example.demo.controller.ws;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__(@JsonCreator))
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Message<T> {

			 long   timestamp;
	@NonNull T      data;
	@NonNull String currency;
	@NonNull String market;
	@NonNull Type   type;

	public static Message<Float> price(long timestamp, float price, String currency, String market) {
		return new Message<>(timestamp, price, currency, market, Type.PRICE);
	}

	public static Message<Trade> trade(long timestamp, float price, float amount, String currency, String market) {
		return new Message<>(timestamp, new Trade(price, amount), currency, market, Type.TRADE);
	}

	public static Message<Float> wallet(float balance, String currency, String market) {
		return new Message<>(Instant.now().toEpochMilli(), balance, currency, market, Type.WALLET);
	}

	public enum Type {
		PRICE, TRADE, WALLET
	}

	@Data
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	@NoArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @__(@JsonCreator))
	public static class Trade {
		float price;
		float amount;
	}
}