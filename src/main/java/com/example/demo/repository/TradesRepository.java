package com.example.demo.repository;

import com.example.demo.domain.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradesRepository extends MongoRepository<Trade, String> {
}
