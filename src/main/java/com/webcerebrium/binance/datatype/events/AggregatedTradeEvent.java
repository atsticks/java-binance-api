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

package com.webcerebrium.binance.datatype.events;

import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.ApiException;
import com.webcerebrium.binance.datatype.HasSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 {
 "e": "aggTrade",		// event type
 "E": 1499405254326,	// event time
 "s": "ETHBTC",			// symbol
 "a": 70232,			// aggregated tradeid
 "p": "0.10281118",		// price
 "q": "8.15632997",		// quantity
 "f": 77489,			// first breakdown trade id
 "l": 77489,			// last breakdown trade id
 "T": 1499405254324,	// trade time
 "m": false,			// whehter buyer is a maker
 "M": true				// can be ignored
 }
 */
@Data
@EqualsAndHashCode(of = {"symbol", "eventTime", "aggregatedTradeId"})
public class AggregatedTradeEvent implements HasSymbol {
    public Long eventTime;
    public String symbol;
    public Long aggregatedTradeId;
    public Double price;
    public Double quantity;
    public Long firstBreakdownTradeId;
    public Long lastBreakdownTradeId;
    public Long tradeTime;
    public boolean isMaker;

    public AggregatedTradeEvent(JsonObject event) throws ApiException {
        eventTime = event.get("E").getAsLong();
        symbol = event.get("s").getAsString();
        aggregatedTradeId = event.get("a").getAsLong();
        price = event.get("p").getAsDouble();
        quantity = event.get("q").getAsDouble();
        firstBreakdownTradeId = event.get("f").getAsLong();
        lastBreakdownTradeId = event.get("l").getAsLong();
        tradeTime = event.get("T").getAsLong();
        isMaker = event.get("m").getAsBoolean();
    }
}
