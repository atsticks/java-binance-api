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
import lombok.EqualsAndHashCode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode(of = {"lastUpdateId"})
public class BinanceEventDepthLevelUpdate {

    private Long lastUpdateId;
    public List<BinanceBidOrAsk> bids = null;
    public List<BinanceBidOrAsk> asks = null;

    public BinanceEventDepthLevelUpdate(JsonObject event){
        this.lastUpdateId = event.get("lastUpdateId").getAsLong();
        this.bids = new LinkedList();
        JsonArray b = event.get("bids").getAsJsonArray();
        Iterator var3 = b.iterator();

        while(var3.hasNext()) {
            JsonElement bidElement = (JsonElement)var3.next();
            this.bids.add(new BinanceBidOrAsk(BinanceBidType.BID, bidElement.getAsJsonArray()));
        }

        this.asks = new LinkedList();
        JsonArray a = event.get("asks").getAsJsonArray();
        Iterator var7 = a.iterator();

        while(var7.hasNext()) {
            JsonElement askElement = (JsonElement)var7.next();
            this.asks.add(new BinanceBidOrAsk(BinanceBidType.ASK, askElement.getAsJsonArray()));
        }
    }

    public Long getLastUpdateId() {
        return lastUpdateId;
    }

    public void setLastUpdateId(Long lastUpdateId) {
        this.lastUpdateId = lastUpdateId;
    }

    public List<BinanceBidOrAsk> getBids() {
        return bids;
    }

    public void setBids(List<BinanceBidOrAsk> bids) {
        this.bids = bids;
    }

    public List<BinanceBidOrAsk> getAsks() {
        return asks;
    }

    public void setAsks(List<BinanceBidOrAsk> asks) {
        this.asks = asks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinanceEventDepthLevelUpdate that = (BinanceEventDepthLevelUpdate) o;

        if (lastUpdateId != null ? !lastUpdateId.equals(that.lastUpdateId) : that.lastUpdateId != null) return false;
        if (bids != null ? !bids.equals(that.bids) : that.bids != null) return false;
        return asks != null ? asks.equals(that.asks) : that.asks == null;
    }

    @Override
    public int hashCode() {
        int result = lastUpdateId != null ? lastUpdateId.hashCode() : 0;
        result = 31 * result + (bids != null ? bids.hashCode() : 0);
        result = 31 * result + (asks != null ? asks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BinanceEventDepthLevelUpdate{" +
                "lastUpdateId=" + lastUpdateId +
                ", bids=" + bids +
                ", asks=" + asks +
                '}';
    }


}
