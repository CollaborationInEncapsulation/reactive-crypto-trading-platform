package com.example.demo.service.bitmex;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor(onConstructor = @__(@JsonCreator))
@JsonIgnoreProperties(ignoreUnknown = true)
public class BitmexMessage<T> {
    String  table;
    String  action;
    List<T> data;

    @Data
    @NoArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalTrade {
        @NonNull
        Date timestamp;
        String symbol;
        String side;
        long   size;
        float  price;
        String tickDirection;
        String trdMatchID;
        long   grossValue;
        float  homeNotional;
        float  foreignNotional;
    }

    @Data
    @NoArgsConstructor(onConstructor = @__(@JsonCreator))
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InternalPrice {
        Date  timestamp;
        float lastPrice;
    }
}
