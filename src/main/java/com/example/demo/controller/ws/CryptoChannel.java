/**
 * Copyright (C) Zoomdata, Inc. 2012-2018. All rights reserved.
 */
package com.example.demo.controller.ws;

import com.example.demo.service.CryptoService;
import com.example.demo.service.WalletService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class CryptoChannel extends TextWebSocketHandler {
    private static final int SEND_TIME_LIMIT = 2 * 60 * 1000;
    private static final int BUFFER_SIZE_LIMIT = 2 * 1024 * 1024;

    private final WebSocketMessageMapper mapper;
    private final List<CryptoService> cryptoServices;
    private final WalletService walletService;

    private final ConcurrentHashMap<WebSocketSession, SessionContext> sessions = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        Message<Message.Trade> tradeRequest = mapper.decode(message.getPayload());

        doUnderUserSession(session,
                () -> cryptoServices.forEach(cs -> cs.trade(tradeRequest))
        );
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        ConcurrentWebSocketSessionDecorator syncSession = syncSession(session);

        List<Callable<Void>> cancellation = cryptoServices.stream()
                .map(service -> service.subscribe(
                        message -> sendMessage(syncSession, message)
                ))
                .collect(Collectors.toList());

        sessions.put(session, new SessionContext(cancellation));

        doUnderUserSession(session, () -> {
            walletService.subscriberForStateStream(
                    message -> sendMessage(syncSession, message)
            );
        });


        log.info("[WS: {}] Connection established", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SessionContext sessionContext = sessions.remove(session);
        if (sessionContext != null) {
            sessionContext.cancel();
        }
        log.info("[WS: {}] Connection closed", session.getId());
    }

    private void sendMessage(WebSocketSession session, Message<?> message) {
        TextMessage outMessage = new TextMessage(mapper.encode(message));
        try {
            if (session.isOpen()) {
                session.sendMessage(outMessage);
                log.info("[WS: {}] << {}", session.getId(), outMessage);
            }
        } catch (IOException e) {
            log.warn("Error while sending message", e);
        }
    }

    private <T> T doUnderUserSession(WebSocketSession session, Callable<T> action) {
        try {
            SecurityContextHolder.getContext()
                    .setAuthentication((Authentication) session.getPrincipal());

            return action.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void doUnderUserSession(WebSocketSession session, Runnable action) {
        doUnderUserSession(session, () -> {
            action.run();
            return null;
        });
    }

    private ConcurrentWebSocketSessionDecorator syncSession(WebSocketSession session) {
        return new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, BUFFER_SIZE_LIMIT);
    }

    @Data
    static class SessionContext {
        private final List<Callable<Void>> cancellation;

        public void cancel() {
            cancellation.forEach(c -> {
                try {
                    c.call();
                } catch (Exception e) {
                    log.warn("Error while cancellation", e);
                }
            });
        }
    }

}
