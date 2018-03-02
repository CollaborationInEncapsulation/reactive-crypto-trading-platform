package com.example.demo.service.local;

import com.example.demo.controller.ws.Message;
import com.example.demo.domain.Wallet;

public class LocalMessageMapper {
	public static Message<Float> walletToMessage(Wallet wallet) {
		return Message.wallet(wallet.getBalance(), wallet.getCurrency(), "Local");
	}
}
