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

import java.util.Objects;

@Data
@EqualsAndHashCode(of = {"symbol", "pairType"})
public class MarketPair implements HasSymbol{

    public enum PairType {
        spot,
        isolated,
        crossmargin
    }

   // Required: base_asset_symbol,list_price,lot_size,quote_asset_symbol,tick_size
    String symbol; //
    String baseSymbol; // base_asset_symbol:
    String quoteSymbol;
    @Getter
    PairType pairType;
    boolean sellAllowed;
    boolean buyAllowed;
    boolean marginTrade;


    @Deprecated
    public MarketPair(JsonObject ob, PairType type) {
        symbol = ob.get("symbol").getAsString();
        baseSymbol = ob.get("base").getAsString();
        quoteSymbol = ob.get("quote").getAsString();
        sellAllowed = ob.get("isSellAllowed").getAsBoolean();
        buyAllowed = ob.get("isBuyAllowed").getAsBoolean();
        marginTrade = ob.get("isMarginTrade").getAsBoolean();
        pairType = Objects.requireNonNull(type);
    }

    @Deprecated
    public MarketPair(String symbol, PairType type) {
        this.pairType = Objects.requireNonNull(type);
        this.symbol = Objects.requireNonNull(symbol);
    }

    public MarketPair(String baseSymbol, String quoteSymbol, PairType type) {
        this.pairType = Objects.requireNonNull(type);
        this.baseSymbol = Objects.requireNonNull(baseSymbol);
        this.quoteSymbol = Objects.requireNonNull(quoteSymbol);
        this.symbol = baseSymbol + quoteSymbol;
    }


}
