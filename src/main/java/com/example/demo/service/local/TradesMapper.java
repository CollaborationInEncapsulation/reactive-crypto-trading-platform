package com.example.demo.service.local;

import com.example.demo.domain.Trade;
import com.example.demo.service.Message;

public class TradesMapper {

	public static Message<Message.Trade> tradeToMessage(Trade trade) {
		return Message.trade(
				trade.getTimestamp(),
				trade.getPrice(),
				trade.getAmount(),
				trade.getCurrency(),
				trade.getMarket()
		);
	}

	public static Trade messageToTrade(Message<Message.Trade> tradeOffer) {
		return new Trade(
				tradeOffer.getTimestamp(),
				tradeOffer.getData().getAmount(),
				tradeOffer.getData().getPrice(),
				tradeOffer.getCurrency(),
				"Local"
		);
	}
}
