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

	public static Wallet create(String owner, String currency) {
		return Wallet.builder()
		             .balance(10)
		             .owner(owner)
		             .currency(currency)
		             .build();
	}
}
