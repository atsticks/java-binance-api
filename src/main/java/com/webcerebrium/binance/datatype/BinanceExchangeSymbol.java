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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.util.*;

/**
 {
     "symbol":"ETHBTC",
     "status":"TRADING",
     "baseAsset":"ETH",
     "baseAssetPrecision":8,
     "quoteAsset":"BTC",
     "quotePrecision":8,
     "orderTypes":["LIMIT","LIMIT_MAKER","MARKET","STOP_LOSS_LIMIT","TAKE_PROFIT_LIMIT"],
     "icebergAllowed":true,
     "filters":
     [
        {"filterType":"PRICE_FILTER","minPrice":"0.00000100","maxPrice":"100000.00000000","tickSize":"0.00000100"},
        {"filterType":"LOT_SIZE","minQty":"0.00100000","maxQty":"100000.00000000","stepSize":"0.00100000"},
        {"filterType":"MIN_NOTIONAL","minNotional":"0.00100000"}
     ]
 }
 */
@Slf4j
@Data
public class BinanceExchangeSymbol {

    String symbol;
    String status;
    String baseAsset;
    Long baseAssetPrecision;
    Long baseCommissionPrecision;
    String quoteAsset;
    Long quoteAssetPrecision;
    Long quoteCommissionPrecision;
    List<BinanceOrderType> orderTypes = new LinkedList<>();
    boolean icebergAllowed;
    boolean ocoAllowed;
    boolean quoteOrderQtyMarketAllowed;
    boolean spotTradingAllowed;
    boolean marginTradingAllowed;
    Map<String, BinanceExchangeFilter> filters = new HashMap<>();
    List<String> permissions = new ArrayList<>();

    public BinanceExchangeSymbol(){}

    public BinanceExchangeSymbol(JsonObject obj) throws BinanceApiException {
        symbol = obj.get("symbol").getAsString();
        status = obj.get("status").getAsString();
        baseAsset = obj.get("baseAsset").getAsString();
        baseAssetPrecision = obj.get("baseAssetPrecision").getAsLong();
        quoteAsset = obj.get("quoteAsset").getAsString();
        quoteAssetPrecision = obj.get("quotePrecision").getAsLong();
        quoteCommissionPrecision = obj.get("quoteCommissionPrecision").getAsLong();
        baseCommissionPrecision = obj.get("baseCommissionPrecision").getAsLong();
        icebergAllowed = obj.get("icebergAllowed").getAsBoolean();
        ocoAllowed = obj.get("ocoAllowed").getAsBoolean();
        quoteOrderQtyMarketAllowed = obj.get("quoteOrderQtyMarketAllowed").getAsBoolean();
        spotTradingAllowed = obj.get("isSpotTradingAllowed").getAsBoolean();
        marginTradingAllowed = obj.get("isMarginTradingAllowed").getAsBoolean();

        if (obj.has("orderTypes") && obj.get("orderTypes").isJsonArray()) {
            JsonArray arrOrderTypes = obj.get("orderTypes").getAsJsonArray();
            orderTypes.clear();
            for (JsonElement entry: arrOrderTypes) {
                orderTypes.add(BinanceOrderType.valueOf(entry.getAsString()));
            }
        }

        if (obj.has("filters") && obj.get("filters").isJsonArray()) {
            JsonArray arrFilters = obj.get("filters").getAsJsonArray();
            filters.clear();
            for (JsonElement entry: arrFilters) {
                BinanceExchangeFilter filter = new BinanceExchangeFilter(entry.getAsJsonObject());
                filters.put(filter.getFilterType(), filter);
            }
        }
    }

    public static class BinanceLotSize extends BinanceExchangeFilter{

        public static BinanceLotSize of(BinanceExchangeFilter f) {
            if(f!=null){
                return new BinanceLotSize(f);
            }
            return null;
        }

        public BinanceLotSize(BinanceExchangeFilter filter) {
            super(filter.getData());
        }

        public Double getMinQty(){
            return getDouble("minQty");
        }

        public Double getMaxQty(){
            return getDouble("maxQty");
        }

        public Double getStepSize(){
            return getDouble("stepSize");
        }

    }
    public static class BinancePriceFilter extends BinanceExchangeFilter{

        public static BinancePriceFilter of(BinanceExchangeFilter f) {
            if(f!=null){
                return new BinancePriceFilter(f);
            }
            return null;
        }

        public BinancePriceFilter(BinanceExchangeFilter filter) {
            super(filter.getData());
        }
        public Double getMinPrice(){
            return getDouble("minPrice");
        }

        public Double getMaxPrice(){
            return getDouble("maxPrice");
        }

        public Double getTickSize(){
            return getDouble("tickSize");
        }

    }
    public static class BinanceMinNotional extends BinanceExchangeFilter{

        public BinanceMinNotional(BinanceExchangeFilter filter) {
            super(filter.getData());
        }

        public static BinanceMinNotional of(BinanceExchangeFilter f) {
            if(f!=null){
                return new BinanceMinNotional(f);
            }
            return null;
        }

        public Double getMinNotionale(){
            return getDouble("minNotional");
        }

        public Boolean getApplyToMarket(){
            return getBoolean("applyToMarket");
        }

        public Integer getAvgPriceMinse(){
            return getInteger("avgPriceMins");
        }

    }


    public BinancePriceFilter getPriceFilter() {
        return BinancePriceFilter.of(filters.get("PRICE_FILTER"));
    }

    public BinanceLotSize getLotSize() {
        return BinanceLotSize.of(filters.get("LOT_SIZE"));
    }

    public BinanceLotSize getMarketLotSize() {
        return BinanceLotSize.of(filters.get("MARKET_LOT_SIZE"));
    }

    public BinanceMinNotional getMinNotional() {
        return BinanceMinNotional.of(filters.get("MIN_NOTIONAL"));
    }

    public Double getMinNotionalValue() {
        if (filters.containsKey("MIN_NOTIONAL")) {
            BinanceExchangeFilter obj = this.getMinNotional();
            return obj.getDouble("minNotional");
        }
        return 0d;
    }

    public Long getMaxNumOrders() {
        if (filters.containsKey("MAX_NUM_ORDERS")) {
            BinanceExchangeFilter obj = filters.get("MAX_NUM_ORDERS");
            return obj.getLong("maxNumOrders");
        }
        return 0L;
    }

}
