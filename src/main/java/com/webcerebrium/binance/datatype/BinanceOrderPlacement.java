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

import com.google.common.base.Strings;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.webcerebrium.binance.api.BinanceApiException;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
public class BinanceOrderPlacement {
    @NonNull
    public BinanceSymbol symbol = null;
    @NonNull
    public BinanceOrderSide side = null;
    public BinanceOrderType type = BinanceOrderType.LIMIT;
    public BinanceTimeInForce timeInForce = BinanceTimeInForce.GOOD_TILL_CANCELLED;
    public Double quantity;
    public Double price;
    public String newClientOrderId = "";
    public Double stopPrice = null;
    public Double icebergQty = null;

    public BinanceOrderPlacement(BinanceSymbol symbol, BinanceOrderSide side) {
        this.symbol = symbol;
        this.side = side;
    }

    public String getAsQuery() throws BinanceApiException {
        StringBuffer sb = new StringBuffer();
        Escaper esc = UrlEscapers.urlFormParameterEscaper();
        if (symbol == null) {
            throw new BinanceApiException("Order Symbol is not set");
        }
        sb.append("&symbol=").append(symbol.toString());
        if (side == null) {
            throw new BinanceApiException("Order side is not set");
        }
        sb.append("&side=").append(side.toString());
        if (type == null) {
            throw new BinanceApiException("Order type is not set");
        }
        sb.append("&type=").append(type.toString());
        if (quantity == null || quantity.compareTo(0d) <= 0) {
            throw new BinanceApiException("Order quantity should be bigger than zero");
        }
        sb.append("&quantity=").append(quantity.toString());

        if (type == BinanceOrderType.MARKET) {
            // price should be skipped for a market order, we are accepting market price
            // so should timeInForce
        } else {
            if (timeInForce == null) {
                throw new BinanceApiException("Order timeInForce is not set");
            }
            sb.append("&timeInForce=").append(timeInForce.toString());
            if (price == null || price.compareTo(0d) <= 0) {
                throw new BinanceApiException("Order price should be bigger than zero");
            }
            sb.append("&price=").append(price.toString());
        }

        if (!Strings.isNullOrEmpty(newClientOrderId)) {
            sb.append("&newClientOrderId=").append(esc.escape(newClientOrderId));
        }
        if (stopPrice != null) {
            sb.append("&stopPrice=").append(stopPrice.toString());
        }
        if (icebergQty != null) {
            sb.append("&icebergQty=").append(icebergQty.toString());
        }
        return sb.toString().substring(1); // skipping the first &
    }
}
