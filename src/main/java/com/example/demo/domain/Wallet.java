package com.example.demo.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
@CompoundIndexes({
		@CompoundIndex(
				name = "owner_currency",
				def = "{'owner': 1, 'currency': 1}",
				unique = true
		)
})
public class Wallet {

	@Id                               String id;

	@Setter(value = AccessLevel.NONE) String owner;
	@Setter(value = AccessLevel.NONE) String currency;
	@Setter(value = AccessLevel.NONE) float  balance;

	public Wallet withdraw(float amount) {
		float nextBalance = balance - amount;

		if (nextBalance >= 0) {
			return Wallet.builder()
			             .id(id)
			             .owner(owner)
			             .currency(currency)
			             .balance(nextBalance)
			             .build();
		}

		throw new NotEnoughMoneyException();
	}

	public Wallet adjust(float amount) {
		return Wallet.builder()
		             .id(id)
		             .owner(owner)
		             .currency(currency)
		             .balance(balance + amount)
		             .build();
	}

	public static Wallet create(String owner, String currency) {
		return Wallet.builder()
		             .balance(10)
		             .owner(owner)
		             .currency(currency)
		             .build();
	}

	public static class NotEnoughMoneyException extends IllegalStateException { }
}
