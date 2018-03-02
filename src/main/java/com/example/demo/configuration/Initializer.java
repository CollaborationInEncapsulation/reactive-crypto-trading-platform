package com.example.demo.configuration;

import java.util.Arrays;

import com.example.demo.domain.Wallet;
import com.example.demo.repository.WalletRepository;
import reactor.core.publisher.Mono;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Initializer {

	@Bean
	public CommandLineRunner commandLineRunner(WalletRepository repository) {
		return args -> repository
				.saveAll(Arrays.asList(
					Wallet.create("admin", "BTC"),
					Wallet.create("admin", "USD"),

					Wallet.create("user", "BTC"),
					Wallet.create("user", "USD")
				))
                .onErrorResume(t -> Mono.empty())
				.blockLast();
	}

}
