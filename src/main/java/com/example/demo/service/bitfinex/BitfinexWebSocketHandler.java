package com.example.demo.service.bitfinex;

import com.example.demo.controller.ws.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class BitfinexWebSocketHandler extends TextWebSocketHandler {
    private static final String TRADES_REQUEST = "{\"event\": \"subscribe\", \"channel\": \"trades\", \"pair\": \"BTCUSD\"}";
    private static final String PRICE_REQUEST  = "{\"event\": \"subscribe\", \"channel\": \"ticker\", \"pair\": \"BTCUSD\"}";

    private final Consumer<Message<?>> consumer;

    public BitfinexWebSocketHandler(Consumer<Message<?>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Bitfinex");
        try {
            session.sendMessage(new TextMessage(TRADES_REQUEST));
            session.sendMessage(new TextMessage(PRICE_REQUEST));
        } catch (IOException e) {
            log.warn("Error while sending interest requests", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Connection to Bitfinex closed with status: {}", status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        if (payload.contains("[") && payload.contains("]")) {
            Message<?>[] decoded = BitfinexMessageMapper.bitfinexToMessage(payload);
            Stream.of(decoded).forEach(msg -> {
                log.info("[Bitfinex] >> {}", msg);
                consumer.accept(msg);
            });
        }
    }

}
