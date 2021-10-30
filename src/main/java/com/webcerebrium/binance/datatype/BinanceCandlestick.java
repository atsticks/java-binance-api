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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;



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

@Getter
@Setter
@EqualsAndHashCode
public class BinanceCandlestick {
    public String symbol;
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

    public BinanceCandlestick(String symbol){
        this.symbol = Objects.requireNonNull(symbol);
    }

    public BinanceCandlestick read(JsonArray jsonArray) throws BinanceApiException {
        this.symbol = Objects.requireNonNull(symbol);
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
        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getMinValue() {
        return low;
    }

    public Double getMaxValue() {
        return high;
    }

    public Double getMediumValue(){
        return getFrameSize() / 2d;
    }

    public Double getFrameSize(){
        return high - low;
    }

    public long getFrameDuration(){
        return closeTime - openTime;
    }

    public Double getDistance(){
        return Math.abs(close - open);
    }

    public Long getStartTime() {
        return openTime;
    }

    public Long getEndTime() {
        return closeTime;
    }

    public String getFrameType() {
        if(close>open){
            return "ASC";
        }else if(open>close){
            return "DESC";
        }
        return "EQUAL";
    }

    public Double getChangePerTimeUnit(TimeUnit timeUnit, long units) {
        double factor = getFrameDuration()/ (double)timeUnit.toMillis(units);
        return getChangeAmount() * factor;
    }

    public Float getChangeRate() {
        return (float)(getChangeAmount() / open);
    }

    public Double getChangeRatePerTimeUnit(TimeUnit timeUnit, long units) {
        double factor = (double)timeUnit.toMillis(units)/getFrameDuration();
        return getChangeRate() * factor;
    }

    public Double getChangeAmount() {
        return close - open;
    }

    @Override
    public String toString() {
        return "BinanceCandlestick{\n" +
                "  symbol                    = '" + symbol + '\'' + "\n" +
                "  type                      = " + getFrameType() +"\n" +
                "  openTime                  = " + openTime +"\n" +
                "  open                      = " + open +"\n" +
                "  high                      = " + high +"\n" +
                "  low                       = " + low +"\n" +
                "  close                     = " + close +"\n" +
                "  volume                    = " + volume +"\n" +
                "  closeTime                 = " + closeTime +"\n" +
                "  quoteAssetVolume          = " + quoteAssetVolume +"\n" +
                "  numberOfTrades            = " + numberOfTrades +"\n" +
                "  takerBuyBaseAssetVolume   = " + takerBuyBaseAssetVolume +"\n" +
                "  takerBuyQuoteAssetVolume  = " + takerBuyQuoteAssetVolume +"\n" +
                "  takerBuyQuoteAssetVolume  = " + takerBuyQuoteAssetVolume +"\n" +
                "  changeAmount              = " + getChangeAmount() +"\n" +
                "  changeRate                = " + getChangeRate()*100 + " %\n" +
                "  changeRate (1h)           = " + getChangeRatePerTimeUnit(TimeUnit.HOURS, 1)*100 + " %\n" +
                '}';
    }
}
