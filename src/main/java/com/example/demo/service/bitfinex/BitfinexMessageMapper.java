package com.example.demo.service.bitfinex;

import java.time.Instant;
import java.util.Arrays;

import com.example.demo.controller.ws.Message;

public class BitfinexMessageMapper {

    private static Message<?> bitfinexToMessage(String[] vals) {
        if (vals.length > 7) {
            return Message.price(
                    Instant.now().toEpochMilli(),
                    Float.valueOf(vals[vals.length - 4]),
                    "BTC",
                    "Bitfinex"
            );
        } else {
            vals = Arrays.copyOfRange(vals, vals.length - 3, vals.length);
            return Message.trade(
                    Long.valueOf(vals[0]),
                    Float.parseFloat(vals[2]),
                    Float.parseFloat(vals[1]),
                    "BTC",
                    "Bitfinex"
            );
        }
    }

    static Message<?>[] bitfinexToMessage(String payload) {
        payload = payload.substring(1, payload.length() - 1).trim();

        if (payload.contains("[") && payload.contains("]")) {
            payload = payload.substring(payload.indexOf("["), payload.lastIndexOf("]"));
            return Arrays.stream(payload.split("\\],\\[|\\[|\\]"))
                         .filter(p -> !p.isEmpty())
                         .map(p -> bitfinexToMessage(p.split(",")))
                         .toArray(Message<?>[]::new);
        } else if (!payload.isEmpty() && !payload.contains("te") && !payload.contains("hb")) {
            return new Message<?>[]{bitfinexToMessage(payload.split(","))};
        } else {
            return new Message<?>[0];
        }
    }
}
