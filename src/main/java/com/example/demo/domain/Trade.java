package com.example.demo.domain;

import com.mongodb.annotations.Immutable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Immutable
@Data
@NoArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Trade {

    @Id      String id;
             long   timestamp;
             float  amount;
             float  price;
    @NonNull String currency;
    @NonNull String market;

    public Trade(long   timestamp,
                 float  amount,
                 float  price,
                 String currency,
                 String market) {
        this(currency, market);
        this.timestamp = timestamp;
        this.amount = amount;
        this.price = price;
    }
}
