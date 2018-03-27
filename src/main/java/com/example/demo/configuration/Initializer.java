package com.example.demo.configuration;

import com.example.demo.domain.Wallet;
import com.example.demo.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class Initializer {

	@Bean
	public CommandLineRunner commandLineRunner(WalletRepository repository) {
		repository.deleteAll();
		return args -> repository
				.saveAll(Arrays.asList(
					Wallet.create("admin", "BTC"),
					Wallet.create("admin", "USD"),

					Wallet.create("user", "BTC"),
					Wallet.create("user", "USD")
				));
	}

}
