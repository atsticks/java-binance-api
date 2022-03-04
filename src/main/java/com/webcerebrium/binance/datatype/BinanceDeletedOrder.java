///*
// * MIT License
// *
// * Copyright (c) 2017 Web Cerebrium
// * Copyright (c) 2021 Anatole Tresch
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package com.webcerebrium.binance.datatype;
//
//import com.google.gson.JsonObject;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.util.Objects;
//
//@Data
//@EqualsAndHashCode(of = {"symbol", "orderId"})
//public class BinanceDeletedOrder {
//
//    String symbol;
//    String origClientOrderId;
//    Long orderId;
//    Long orderListId;
//    String clientOrderId;
//    Double price;
//    Double origQty;
//    Double executedQty;
//    Double cummulativeQuoteQty;
//    BinanceOrderStatus status;
//    BinanceTimeInForce timeInForce;
//    BinanceOrderType type;
//    BinanceOrderSide side;
//
//    public BinanceDeletedOrder(String symbol, long orderId) {
//        this.symbol = Objects.requireNonNull(symbol);
//        this.orderId = orderId;
//    }
//
//    public BinanceDeletedOrder(JsonObject ob) {
//        symbol = ob.get("symbol").getAsString();
//        origClientOrderId = ob.get("origClientOrderId").getAsString();
//        clientOrderId = ob.get("clientOrderId").getAsString();
//        status = BinanceOrderStatus.valueOf(ob.get("status").getAsString());
//        timeInForce = BinanceTimeInForce.valueOf(ob.get("timeInForce").getAsString());
//        type = BinanceOrderType.valueOf(ob.get("type").getAsString());
//        side = BinanceOrderSide.valueOf(ob.get("side").getAsString());
//    }
//}
