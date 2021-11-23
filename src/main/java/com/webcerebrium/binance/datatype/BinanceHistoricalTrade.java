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

// Internal variables are not human readable. So this class contains better readable getters

//{
//        "id":345196462,
//        "price":"9638.99000000",
//        "qty":"0.02077200",
//        "quoteQty":"0.02077200",
//        "time":1592887772684,
//        "isBuyerMaker":true,
//        "isBestMatch":true
//        }
import lombok.Data;


@Data
public class BinanceHistoricalTrade {

    public long id;
    public Double price;
    public Double qty;
    public  Double quoteQty;
    public long time;
    public boolean isBuyerMaker;
    public boolean isBestMatch;

    public long getTradeId() { return id; }
    public Double getQuantity() { return qty; }
    public Double getQuoteQuantity() { return quoteQty; }
    public long getTimestamp() { return time; }
    public boolean wasMaker() { return isBuyerMaker; }
    public boolean wasBestPrice() { return isBestMatch; }

    @Override
    public String toString() {
        return "BinanceHistoricalTrade{" +
            "tradeId=" + getTradeId() +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", quoteQuantity=" + getQuoteQuantity() +
            ", timestamp=" + getTimestamp() +
            ", maker=" + wasMaker() +
            ", bestPrice=" + wasBestPrice() +
            '}';
    }
}
