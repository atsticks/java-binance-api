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
     "symbol": "LTCBTC",
     "orderId": 1,
     "clientOrderId": "myOrder1",
     "price": "0.1",
     "origQty": "1.0",
     "executedQty": "0.0",
     "status": "NEW",
     "timeInForce": "GTC",
     "type": "LIMIT",
     "side": "BUY",
     "stopPrice": "0.0",
     "icebergQty": "0.0",
     "time": 1499827319559
 }
 */

import lombok.Data;



@Data
public class BinanceOrder {
    public String symbol;
    public Long orderId;
    public String clientOrderId;
    public Double price;
    public Double origQty;
    public Double executedQty;
    public BinanceOrderStatus status;
    public BinanceTimeInForce timeInForce;
    public BinanceOrderType type;
    public BinanceOrderSide side;
    public Double stopPrice;
    public Double icebergQty;
    public Long time;
}
