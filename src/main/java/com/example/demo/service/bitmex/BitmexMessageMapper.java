package com.example.demo.service.bitmex;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.controller.ws.Message;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BitmexMessageMapper {

	public static List<Message<?>> bitmexToMessage(String payload) {
		try {
			return payload.contains("Sell") || payload.contains("Buy") ?
					new ObjectMapper().<BitmexMessage<BitmexMessage.InternalTrade>>readValue(
							payload,
							new TypeReference<BitmexMessage<BitmexMessage.InternalTrade>>() {
							}).getData()
					          .stream()
					          .map(d -> Message.trade(d.getTimestamp()
					                                   .toInstant()
					                                   .toEpochMilli(),
							          d.getPrice(),
							          d.getHomeNotional(),
							          "BTC",
							          "Bitmex"))
					          .collect(Collectors.toList()) :
					new ObjectMapper().<BitmexMessage<BitmexMessage.InternalPrice>>readValue(
							payload,
							new TypeReference<BitmexMessage<BitmexMessage.InternalPrice>>() {
							}).getData()
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
