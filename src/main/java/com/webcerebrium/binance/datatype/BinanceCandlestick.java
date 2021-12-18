/*
 * MIT License
 *
 * Copyright (c) 2017 Web Cerebrium
 * Copyright (c) 2021 Anatole Tresch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.webcerebrium.binance.datatype;

// Internal variables are not human readable. So this class contains better readable getters

import com.google.gson.JsonArray;
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.Comparator;
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

@Data
@EqualsAndHashCode(of = {"symbol", "interval", "openTime"})
public class BinanceCandlestick implements Comparable<BinanceCandlestick> {
    String symbol;
    BinanceInterval interval;
    Long openTime = null;
    Double open = null;
    Double high = null;
    Double low = null;
    Double close = null;
    Double volume = null;
    Long closeTime = null;
    Double quoteAssetVolume = null;
    Long numberOfTrades = null;
    Double takerBuyBaseAssetVolume = null;
    Double takerBuyQuoteAssetVolume = null;
    long timestamp = System.currentTimeMillis();

    public BinanceCandlestick(String symbol, BinanceInterval interval){
        this.symbol = Objects.requireNonNull(symbol);
        this.interval = Objects.requireNonNull(interval);
    }

    public BinanceCandlestick read(JsonArray jsonArray, BinanceInterval interval) throws BinanceApiException {
        this.symbol = Objects.requireNonNull(symbol);
        this.interval = Objects.requireNonNull(interval);
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
        if(openTime!=null && closeTime!=null) {
            return closeTime - openTime;
        }
        return 0;
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
        if(close!=null && open!=null) {
            if (close > open) {
                return "ASC";
            } else if (open > close) {
                return "DESC";
            }
        }
        return "EQUAL";
    }

    public Double getChangePerTimeUnit(TimeUnit timeUnit, long units) {
        double factor = getFrameDuration()/ (double)timeUnit.toMillis(units);
        return getChangeAmount() * factor;
    }

    public Float getChangeRate() {
        if(open!=null) {
            return (float) (getChangeAmount() / open);
        }
        return 0.0f;
    }

    public Double getChangeRatePerTimeUnit(TimeUnit timeUnit, long units) {
        double factor = (double)timeUnit.toMillis(units)/getFrameDuration();
        return getChangeRate() * factor;
    }

    public Double getChangeAmount() {
        if(close!=null && open!=null) {
            return close - open;
        }
        return 0.0;
    }

    @Override
    public int compareTo(BinanceCandlestick o) {
        if(o==null){
            return -1;
        }
        if(getClose() == null && o.getClose()==null){
            return 0;
        }else if(getClose()!=null){
            return -1;
        }else if(o.getClose()!=null){
            return 1;
        }
        return getClose().compareTo(o.getClose());
    }

    @Override
    public String toString() {
        return "BinanceCandlestick{\n" +
                "  symbol                    = '" + symbol + '\'' + "\n" +
                "  interval                  = '" + interval + '\'' + "\n" +
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
