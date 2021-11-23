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
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.util.HashSet;
import java.util.Set;

@Data
@Slf4j
public class BinanceExchangeProduct {
    boolean active;
    Double activeBuy;
    Double activeSell;
    String baseAsset;
    String baseAssetUnit;
    Double close;
    Long decimalPlaces;
    Double high;
    Long lastAggTradeId;
    Double low;
    String matchingUnitType;
    Double minQty;
    Double minTrade;
    Double open;
    Double prevClose;
    String quoteAsset;
    String quoteAssetUnit;
    String status;
    BinanceSymbol symbol;
    Double tickSize;
    Double tradedMoney;
    Double volume;
    Double withdrawFee;

    public BinanceExchangeProduct() {
    }

    private void jsonExpect(JsonObject obj, Set<String> fields) throws BinanceApiException {
        Set<String> missing = new HashSet<>();
        for (String f: fields) { if (!obj.has(f) || obj.get(f).isJsonNull()) missing.add(f); }
        if (missing.size() > 0) {
            log.warn("Missing fields {} in {}", missing.toString(), obj.toString());
            throw new BinanceApiException("Missing fields " + missing.toString());
        }
    }

    private Double safeDecimal(JsonObject obj, String field) {
        if (obj.has(field) && obj.get(field).isJsonPrimitive() && obj.get(field) != null) {
            try {
                return obj.get(field).getAsDouble();
            } catch (java.lang.NumberFormatException nfe) {
                log.info("Number format exception in field={} value={} trade={}", field, obj.get(field), obj.toString());
            }
        }
        return null;
    }


    public BinanceExchangeProduct(JsonObject obj) throws BinanceApiException {

        symbol = BinanceSymbol.valueOf(obj.get("symbol").getAsString());
        active = obj.get("active").getAsBoolean();

        quoteAsset = obj.get("quoteAsset").getAsString();
        quoteAssetUnit = obj.get("quoteAssetUnit").getAsString();
        status = obj.get("status").getAsString();
        baseAsset = obj.get("baseAsset").getAsString();
        baseAssetUnit = obj.get("baseAssetUnit").getAsString();
        matchingUnitType = obj.get("matchingUnitType").getAsString();

        decimalPlaces  = obj.get("decimalPlaces").getAsLong();
        lastAggTradeId  = obj.get("lastAggTradeId").getAsLong();

        activeBuy = safeDecimal(obj, "activeBuy");
        activeSell = safeDecimal(obj, "activeSell");
        close = safeDecimal(obj, "close");
        high = safeDecimal(obj, "high");
        low = safeDecimal(obj, "low");
        minQty = safeDecimal(obj, "minQty");
        minTrade = safeDecimal(obj, "minTrade");
        open = safeDecimal(obj, "open");
        prevClose = safeDecimal(obj, "prevClose");

        tickSize = safeDecimal(obj, "tickSize");
        tradedMoney = safeDecimal(obj, "tradedMoney");
        volume = safeDecimal(obj, "volume");
        withdrawFee = safeDecimal(obj, "withdrawFee");
    }
}
