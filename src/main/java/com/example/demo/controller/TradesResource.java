package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.service.CryptoService;
import com.example.demo.service.Message;
import com.example.demo.service.local.LocalCryptoService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/trades")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TradesResource {

	private final LocalCryptoService  localCryptoService;
	private final List<CryptoService> cryptoServices;

	@GetMapping
	public List<Message<?>> total(@RequestParam("timestamp") Long timestamp) {
		return cryptoServices.parallelStream()
		                     .flatMap(cs -> cs.retrieve(timestamp).stream())
		                     .collect(Collectors.toList());
	}

    @PostMapping
    public void offer(@RequestBody Message<Message.Trade> trade) {
	    localCryptoService.store(trade);
    }
}
