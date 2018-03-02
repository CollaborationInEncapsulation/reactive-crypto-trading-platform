package com.example.demo.repository;

import com.example.demo.domain.Trade;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradesRepository extends ReactiveMongoRepository<Trade, String> {
}
