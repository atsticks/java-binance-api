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
import com.webcerebrium.binance.api.ApiException;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Data
public class ExchangeStats {
    List<ExchangeProduct> products = new LinkedList<>();

    public ExchangeStats() {
    }

    public ExchangeStats(JsonObject response) throws ApiException {
        if (!response.has("data")) {
            throw new ApiException("Missing restrictions in response object while trying to get restrictions");
        }
        JsonArray pairs = response.get("data").getAsJsonArray();
        products.clear();
        for (JsonElement entry: pairs) {
            ExchangeProduct symbol = new ExchangeProduct(entry.getAsJsonObject());
            products.add(symbol);
        }
    }

    public List<ExchangeProduct> getMarketsOf(String coin) {
        List<ExchangeProduct> result = new LinkedList<>();
        for (int i = 0; i < products.size(); i++ ) {
            ExchangeProduct tradingSymbol = products.get(i);
            if (!tradingSymbol.isActive()) continue;
            if (tradingSymbol.getSymbol().contains(coin)) {
                result.add(tradingSymbol);
            }
        }
        return result;
    }

    public Set<String> getSymbolsOf(String coin) throws ApiException {
        List<ExchangeProduct> coins = getMarketsOf(coin);
        Set<String> result = new TreeSet<>();
        for (ExchangeProduct sym: coins) {
            result.add(sym.getSymbol());
        }
        return result;
    }

    public Set<String> getCoinsOf(String coin) throws ApiException {
        List<ExchangeProduct> coins = getMarketsOf(coin);
        Set<String> result = new TreeSet<>();
        for (ExchangeProduct sym: coins) {
            result.add(BinanceSymbol.getOpposite(sym.getSymbol(), coin));
        }
        return result;
    }
}
