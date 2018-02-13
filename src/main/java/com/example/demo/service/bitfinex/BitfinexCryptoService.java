package com.example.demo.service.bitfinex;

import java.util.ArrayList;
import java.util.Arrays;
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
public class BitfinexCryptoService implements CryptoService {
    private final RestTemplate restTemplate;

    @Autowired
    public BitfinexCryptoService(RestTemplateBuilder templateBuilder) {
        restTemplate = templateBuilder.rootUri("https://api.bitfinex.com/v2")
                                      .build();
    }

    @Override
    @Retryable(maxAttempts = 1)
    public List<Message<?>> retrieve(Long timestamp) {
        Message<?>[] trades = BitfinexMessageMapper.bitfinexToMessage(
                restTemplate.getForObject(
                		"/trades/tBTCUSD/hist?start={start}",
		                String.class,
		                timestamp
                )
        );
        Message<?>[] prices = BitfinexMessageMapper.bitfinexToMessage(
                restTemplate.getForObject(
                		"/ticker/tBTCUSD",
		                String.class
                )
        );
        List<Message<?>> result = new ArrayList<>();

        result.addAll(Arrays.asList(trades));
        result.addAll(Arrays.asList(prices));

        return result;
    }

	@Recover
	List<Message<?>> fallbackRetrieve(Exception e) {
		return Collections.emptyList();
	}
}