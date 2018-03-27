package com.example.demo.service;

import com.example.demo.controller.ws.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public abstract class BaseCryptoService implements CryptoService {
    private final ConcurrentHashMap<String, Consumer<Message<?>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public Callable<Void> subscribe(Consumer<Message<?>> subscriber) {
        String id = UUID.randomUUID().toString();
        subscribers.put(id, subscriber);
        return () -> {
            subscribers.remove(id);
            return null;
        };
    }

    protected void broadcast(Message<?> message) {
        try {
            subscribers.values()
                    .forEach(subs -> subs.accept(message));
        } catch (Exception e) {
            log.error("Error while broadcasting message", e);
        }
    }
}
