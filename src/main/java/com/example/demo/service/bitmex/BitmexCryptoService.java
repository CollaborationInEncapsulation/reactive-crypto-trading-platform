package com.example.demo.service.bitmex;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.demo.service.CryptoService;
import com.example.demo.service.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BitmexCryptoService implements CryptoService {
    private final RestTemplate restTemplate;

    @Autowired
    public BitmexCryptoService(RestTemplateBuilder templateBuilder) {
        restTemplate = templateBuilder.rootUri("https://www.bitmex.com/api/v1")
                                      .build();
    }

    @Override
    @Retryable(maxAttempts = 1)
    public List<Message<?>> retrieve(Long timestamp) {
        List<Message<?>> trades = BitmexMessageMapper.bitmexToMessage(
                restTemplate.getForObject(
                        "/trade?symbol=XBTUSD&reverse=true&startTime={startTime}",
                        String.class,
                        Instant.ofEpochMilli(timestamp).toString()
                )
        );
        List<Message<?>> prices = BitmexMessageMapper.bitmexToMessage(
                restTemplate.getForObject(
                        "/instrument?symbol=XBTUSD",
                        String.class
                )
        );
        List<Message<?>> result = new ArrayList<>();

        result.addAll(trades);
        result.addAll(prices);

        return result;
    }

    @Recover
    List<Message<?>> fallbackRetrieve(Exception e) {
        return Collections.emptyList();
    }
}