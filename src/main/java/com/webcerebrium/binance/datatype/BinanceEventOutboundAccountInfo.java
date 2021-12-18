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
/*
{
   "e": "outboundAccountInfo",		// event type
   "E": 1499405658849,				// event time
   "m": 0,
   "t": 0,
   "b": 0,
   "s": 0,
   "T": true,
   "W": true,
   "D": true,
   "B": [  							// balances
       {
           "a": "LTC",				// asset
           "f": "17366.18538083",	// available balance
           "l": "0.00000000"		// locked by open orders
       },
       {
           "a": "BTC",
           "f": "10537.85314051",
           "l": "2.19464093"
       },
       {
           "a": "ETH",
           "f": "17902.35190619",
           "l": "0.00000000"
       },
       {
           "a": "BNC",
           "f": "1114503.29769312",
           "l": "0.00000000"
       },
       {
           "a": "NEO",
           "f": "0.00000000",
           "l": "0.00000000"
       }
   ]
}
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"eventTime"})
public class BinanceEventOutboundAccountInfo {
    public Long eventTime;
    public Long makerCommission;
    public Long takerCommission;
    public Long buyerCommission;
    public Long sellerCommission;
    public boolean canTrade;
    public boolean canWithdraw;
    public boolean canDeposit;
    public List<BinanceAsset> balances;

    public BinanceEventOutboundAccountInfo(JsonObject event) {
        eventTime = event.get("E").getAsLong();

        makerCommission = event.get("m").getAsLong();
        takerCommission = event.get("t").getAsLong();
        buyerCommission = event.get("b").getAsLong();
        sellerCommission = event.get("s").getAsLong();
        canTrade = event.get("T").getAsBoolean();
        canWithdraw = event.get("W").getAsBoolean();
        canDeposit = event.get("D").getAsBoolean();

        balances = new LinkedList<>();
        JsonArray b = event.get("B").getAsJsonArray();
        for (JsonElement asset : b) {
            JsonObject ob = asset.getAsJsonObject();
            BinanceAsset basset = new BinanceAsset(ob.get("name").getAsString());
            basset.read(ob);
            balances.add(basset);
        }
    }
}
