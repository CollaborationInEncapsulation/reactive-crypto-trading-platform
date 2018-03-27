package com.example.demo.repository;

import com.example.demo.domain.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, String> {

	Collection<Wallet> findAllByOwner(String owner);

	Wallet findByOwnerAndCurrency(String owner, String currency);
}
