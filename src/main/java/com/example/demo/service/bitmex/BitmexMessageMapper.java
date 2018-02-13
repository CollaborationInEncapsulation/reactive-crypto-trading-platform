package com.example.demo.service.bitmex;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.service.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BitmexMessageMapper {

	public static List<Message<?>> bitmexToMessage(String payload) {
		try {
			return payload.contains("Sell") || payload.contains("Buy") ?
					new ObjectMapper().<List<BitmexMessage.InternalTrade>>readValue(
							payload,
							new TypeReference<List<BitmexMessage.InternalTrade>>() {
							})
					          .stream()
					          .map(d -> Message.trade(d.getTimestamp()
					                                   .toInstant()
					                                   .toEpochMilli(),
							          d.getPrice(),
							          d.getHomeNotional(),
							          "BTC",
							          "Bitmex"))
					          .collect(Collectors.toList()) :
					new ObjectMapper().<List<BitmexMessage.InternalPrice>>readValue(
							payload,
							new TypeReference<List<BitmexMessage.InternalPrice>>() {
							})
					          .stream()
					          .filter(d -> d.getLastPrice() > 0)
					          .map(d -> Message.price(d.getTimestamp()
					                                   .toInstant()
					                                   .toEpochMilli(),
							          d.getLastPrice(),
							          "BTC",
							          "Bitmex"))
					          .collect(Collectors.toList());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
