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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.ApiException;
import com.webcerebrium.binance.datatype.BidOrAsk;
import com.webcerebrium.binance.datatype.BidType;
import com.webcerebrium.binance.datatype.HasSymbol;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

/*
	{
        "e": "depthUpdate",						// event type
        "E": 1499404630606, 					// event time
        "s": "ETHBTC", 							// symbol
        "u": 7913455, 							// updateId to sync up with updateid in /api/v1/depth
        "b": [									// bid depth delta
            [
                "0.10376590", 					// price (need to upate the quantity on this price)
                "59.15767010", 					// quantity
                []								// can be ignored
            ],
        ],
        "a": [									// ask depth delta
            [
                "0.10376586", 					// price (need to upate the quantity on this price)
                "159.15767010", 				// quantity
                []								// can be ignored
            ],
            [
                "0.10383109",
                "345.86845230",
                []
            ],
            [
                "0.10490700",
                "0.00000000", 					//quantitiy=0 means remove this level
                []
            ]
        ]
    }
 */
@Data
@EqualsAndHashCode(of = {"symbol", "eventTime", "updateId"})
public class DepthUpdateEvent implements HasSymbol {
    public Long eventTime;
    public String symbol;
    public Long updateId;
    public List<BidOrAsk> bids = null;
    public List<BidOrAsk> asks = null;

    public DepthUpdateEvent(JsonObject event) throws ApiException {
        eventTime = event.get("E").getAsLong();
        symbol = event.get("s").getAsString();
        updateId = event.get("u").getAsLong();

        bids = new LinkedList<>();
        JsonArray b = event.get("b").getAsJsonArray();
        for (JsonElement bidElement : b) {
            bids.add( new BidOrAsk(BidType.BID, bidElement.getAsJsonArray()));
        }
        asks = new LinkedList<>();
        JsonArray a = event.get("a").getAsJsonArray();
        for (JsonElement askElement : a) {
            asks.add( new BidOrAsk(BidType.ASK, askElement.getAsJsonArray()));
        }
    }
}
