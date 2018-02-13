package com.example.demo.repository;

import com.example.demo.domain.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {

	Flux<Wallet> findAllByOwner(String owner);

	Mono<Wallet> findByOwnerAndCurrency(String owner, String currency);
}
