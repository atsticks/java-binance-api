package com.webcerebrium.binance.datatype;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

// Internal variables are not human readable. So this class contains better readable getters

//{
//        "id":345196462,
//        "price":"9638.99000000",
//        "qty":"0.02077200",
//        "quoteQty":"0.02077200",
//        "time":1592887772684,
//        "isBuyerMaker":true,
//        "isBestMatch":true
//        }
import lombok.Data;


@Data
public class BinanceHistoricalTrade {

    public long id;
    public Double price;
    public Double qty;
    public  Double quoteQty;
    public long time;
    public boolean isBuyerMaker;
    public boolean isBestMatch;

    public long getTradeId() { return id; }
    public Double getQuantity() { return qty; }
    public Double getQuoteQuantity() { return quoteQty; }
    public long getTimestamp() { return time; }
    public boolean wasMaker() { return isBuyerMaker; }
    public boolean wasBestPrice() { return isBestMatch; }

    @Override
    public String toString() {
        return "BinanceHistoricalTrade{" +
            "tradeId=" + getTradeId() +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", quoteQuantity=" + getQuoteQuantity() +
            ", timestamp=" + getTimestamp() +
            ", maker=" + wasMaker() +
            ", bestPrice=" + wasBestPrice() +
            '}';
    }
}
