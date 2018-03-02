package com.example.demo.service.local;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import com.example.demo.controller.ws.Message;
import com.example.demo.domain.Trade;
import com.example.demo.domain.Wallet;
import com.example.demo.repository.TradesRepository;
import com.example.demo.service.CryptoService;
import com.example.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LocalCryptoService implements CryptoService {

	private final DirectProcessor<Message<?>> stream = DirectProcessor.create();
	private final TradesRepository            tradesRepository;

	@Override
	public Flux<Message<?>> stream() {
		return stream;
	}

	@Override
	@PreAuthorize("isAuthenticated()")
	public Mono<Void> trade(Flux<Message<Message.Trade>> tradeOffer, WalletService walletService) {
		return tradeOffer
				.onBackpressureBuffer()
				.flatMap(trade ->
				walletService.withdraw(trade)
                             .then(doTrade(trade))
				             .then(walletService.adjust(trade))
				             .then(doStoreTrade(trade))
				             .onErrorResume(Wallet.NotEnoughMoneyException.class, t -> Mono.empty())
				             .onErrorResume(t -> walletService.rollback(trade).then(Mono.empty()))
				)
                .map(LocalMessageMapper::tradeToMessage)
                .doOnNext(stream.sink()::next)
                .then();
	}

	private Mono<Void> doTrade(Message<Message.Trade> trade) {
		return Mono.just(trade)
		           .delayElement(Duration.ofMillis(ThreadLocalRandom.current()
		                                                            .nextInt(2000)))
		           .timeout(Duration.ofMillis(1000))
		           .then();
	}

	private Mono<Trade> doStoreTrade(Message<Message.Trade> tradeMessage) {
		return tradesRepository.save(LocalMessageMapper.messageToTrade(tradeMessage))
		                       .timeout(Duration.ofSeconds(2))
		                       .retryWhen(e -> e.zipWith(Flux.range(0,  Integer.MAX_VALUE))
		                                        .delayElements(Duration.ofMillis(200)))
		                       .publishOn(Schedulers.parallel());
	}
}
