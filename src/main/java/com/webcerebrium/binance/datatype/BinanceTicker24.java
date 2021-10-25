package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;

@Data
public final class BinanceTicker24 {
    private String symbol;
    private Double priceChange;
    private Double priceChangePercent;
    private Double weightedAvgPrice;
    private Double prevClosePrice;
    private Double lastPrice;
    private Double lastQty;
    private Double bidPrice;
    private Double bidQty;
    private Double askPrice;
    private Double askQty;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double volume;
    private Double quoteVolume;
    private Long openTime;
    private Long closeTime;
    private Long firstId;
    private Long lastId;
    private Integer count;

    public void read(JsonObject ob){
        priceChange = ob.get("priceChange").getAsDouble();
        priceChangePercent = ob.get("priceChangePercent").getAsDouble();
        weightedAvgPrice = ob.get("weightedAvgPrice").getAsDouble();
        volume = ob.get("volume").getAsDouble();
        quoteVolume = ob.get("quoteVolume").getAsDouble();
        askPrice = ob.get("askPrice").getAsDouble();
        askQty = ob.get("askQty").getAsDouble();
        bidPrice = ob.get("bidPrice").getAsDouble();
        bidQty = ob.get("bidQty").getAsDouble();
        highPrice = ob.get("highPrice").getAsDouble();
        lastPrice = ob.get("lastPrice").getAsDouble();
        lastQty = ob.get("lastQty").getAsDouble();
        lowPrice = ob.get("lowPrice").getAsDouble();
        prevClosePrice = ob.get("prevClosePrice").getAsDouble();
        openPrice = ob.get("openPrice").getAsDouble();
        closeTime = ob.get("closeTime").getAsLong();
        openTime = ob.get("openTime").getAsLong();
        firstId = ob.get("firstId").getAsLong();
        lastId = ob.get("lastId").getAsLong();
        count = ob.get("count").getAsInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinanceTicker24 ticker24 = (BinanceTicker24) o;
        return symbol.equals(ticker24.symbol) && Objects.equals(openTime, ticker24.openTime) && Objects.equals(closeTime, ticker24.closeTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, openTime, closeTime);
    }

}