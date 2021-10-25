package com.webcerebrium.binance.datatype;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

// Internal variables are not human readable. So this class contains better readable getters

import com.google.gson.JsonArray;
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Data;



/*
  [
    1499040000000,      // 0 Open time
    "0.01634790",       // 1 Open
    "0.80000000",       // 2 High
    "0.01575800",       // 3 Low
    "0.01577100",       // 4 Close
    "148976.11427815",  // 5 Volume
    1499644799999,      // 6 Close time
    "2434.19055334",    // 7 Quote asset volume
    308,                // 8 Number of trades
    "1756.87402397",    // 9 Taker buy base asset volume
    "28.46694368",      // 10 Taker buy quote asset volume
    "17928899.62484339" // 11 Can be ignored
  ]
*/

@Data
public class BinanceCandlestick {

    public Long openTime = null;
    public Double open = null;
    public Double high = null;
    public Double low = null;
    public Double close = null;
    public Double volume = null;
    public Long closeTime = null;
    public Double quoteAssetVolume = null;
    public Long numberOfTrades = null;
    public Double takerBuyBaseAssetVolume = null;
    public Double takerBuyQuoteAssetVolume = null;

    public BinanceCandlestick(JsonArray jsonArray) throws BinanceApiException {
        if (jsonArray.size() < 11) {
            throw new BinanceApiException("Error reading candlestick, 11 parameters expected, "
                    + jsonArray.size() + " found");
        }
        setOpenTime(jsonArray.get(0).getAsLong());
        setOpen(jsonArray.get(1).getAsDouble());
        setHigh(jsonArray.get(2).getAsDouble());
        setLow(jsonArray.get(3).getAsDouble());
        setClose(jsonArray.get(4).getAsDouble());
        setVolume(jsonArray.get(5).getAsDouble());
        setCloseTime(jsonArray.get(6).getAsLong());
        setQuoteAssetVolume(jsonArray.get(7).getAsDouble());
        setNumberOfTrades(jsonArray.get(8).getAsLong());
        setTakerBuyBaseAssetVolume(jsonArray.get(9).getAsDouble());
        setTakerBuyQuoteAssetVolume(jsonArray.get(10).getAsDouble());
    }
}
