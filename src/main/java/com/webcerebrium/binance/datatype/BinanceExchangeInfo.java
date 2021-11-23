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

/*
{"timezone":"UTC","serverTime":1515514334979,
        "rateLimits":[
        {"rateLimitType":"REQUESTS","interval":"MINUTE","limit":1200},
        {"rateLimitType":"ORDERS","interval":"SECOND","limit":10},
        {"rateLimitType":"ORDERS","interval":"DAY","limit":100000}
        ],
        "exchangeFilters":[],"symbols":[]
}
*/
@Data
@Slf4j
public class BinanceExchangeInfo {
    String timezone = null;
    Long serverTime = 0L;
    List<BinanceRateLimit> rateLimits = new LinkedList<>();
    List<JsonObject> exchangeFilters = new LinkedList<>(); // missing proper documentation on that yet
    Map<String,BinanceExchangeSymbol> symbols = new HashMap<>();

    public BinanceExchangeInfo(JsonObject obj) throws BinanceApiException {
        timezone = obj.get("timezone").getAsString();
        serverTime = obj.get("serverTime").getAsLong();

        if (obj.has("rateLimits") && obj.get("rateLimits").isJsonArray()) {
            JsonArray arrRateLimits = obj.get("rateLimits").getAsJsonArray();
            rateLimits.clear();
            for (JsonElement entry: arrRateLimits) {
                BinanceRateLimit limit = new BinanceRateLimit(entry.getAsJsonObject());
                rateLimits.add(limit);
            }
        }
        if (obj.has("exchangeFilters") && obj.get("exchangeFilters").isJsonArray()) {
            JsonArray arrExchangeFilters = obj.get("exchangeFilters").getAsJsonArray();
            exchangeFilters.clear();
            for (JsonElement entry: arrExchangeFilters) {
                exchangeFilters.add(entry.getAsJsonObject());
            }
        }
        if (obj.has("symbols") && obj.get("symbols").isJsonArray()) {
            JsonArray arrSymbols = obj.get("symbols").getAsJsonArray();
            symbols.clear();
            for (JsonElement entry: arrSymbols) {
                JsonObject jsonObject = entry.getAsJsonObject();
                if (!jsonObject.has("symbol")) continue;
                String sym = jsonObject.get("symbol").getAsString();
                if (sym.equals("123456")) continue; // some special symbol that doesn't fit

                BinanceExchangeSymbol symbol = new BinanceExchangeSymbol(jsonObject);
                symbols.put(symbol.getSymbol(), symbol);
            }
        }
    }

    public BinanceExchangeSymbol getSymbol(String symbol){
        return this.symbols.get(symbol);
    }

    public Set<String> getSymbols(){
        return this.symbols.keySet();
    }
}
