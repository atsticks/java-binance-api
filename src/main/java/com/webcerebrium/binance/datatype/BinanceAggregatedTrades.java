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

//  {
// "a": 26129,         // Aggregate tradeId
// "p": "0.01633102",  // Price
// "q": "4.70443515",  // Quantity
// "f": 27781,         // First tradeId
// "l": 27781,         // Last tradeId
// "T": 1498793709153, // Timestamp
// "m": true,          // Was the buyer the maker?
// "M": true           // Was the trade the best price match?
// }

import lombok.Data;



@Data
public class BinanceAggregatedTrades {

    long a;
    Double p;
    Double q;
    long f;
    long l;
    long T;
    boolean m;
    boolean M;

    public long getTradeId() { return a; }
    public Double getPrice() { return p; }
    public Double getQuantity() { return q; }
    public long getFirstTradeId() { return f; }
    public long getLastTradeId() { return l; }
    public long getTimestamp() { return T; }
    public boolean wasMaker() { return m; }
    public boolean wasBestPrice() { return M; }

    @Override
    public String toString() {
        return "BinanceAggregatedTrades{" +
            "tradeId=" + getTradeId() +
            ", price=" + getPrice() +
            ", quantity=" + getQuantity() +
            ", firstTradeId=" + getFirstTradeId() +
            ", lastTradeId=" + getLastTradeId() +
            ", timestamp=" + getTimestamp() +
            ", maker=" + wasMaker() +
            ", bestPrice=" + wasBestPrice() +
            '}';
    }
}
