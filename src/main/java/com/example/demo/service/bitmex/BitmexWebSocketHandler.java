package com.example.demo.service.bitmex;

import com.example.demo.controller.ws.Message;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
public class BitmexWebSocketHandler extends TextWebSocketHandler {
    private static final int SKIP_FIRST_MESSAGES = 6;

    private final AtomicInteger messagesToSkip = new AtomicInteger(SKIP_FIRST_MESSAGES);
    private final Consumer<Message<?>> consumer;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Connected to Bitmex");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("Connection to Bitmex closed with status: {}", status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        if (messagesToSkip.decrementAndGet() > 0) {
            log.info("[Bitmex] >> Skipping message...");
        } else {
            List<Message<?>> decoded = BitmexMessageMapper.bitmexToMessage(message.getPayload());
            decoded.forEach(msg -> {
                log.info("[Bitmex] >> {}", msg);
                consumer.accept(msg);
            });
        }
    }
}
