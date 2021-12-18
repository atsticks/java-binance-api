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

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;

@Data
@EqualsAndHashCode(of = {"symbol", "openTime"})
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