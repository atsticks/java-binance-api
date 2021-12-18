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
import lombok.*;

import java.util.Objects;


@Data
@RequiredArgsConstructor
public class BinanceOrderPlacement {
    @NonNull
    public String symbol;
    @NonNull
    BinanceOrderSide side;
    BinanceOrderType type = BinanceOrderType.LIMIT;
    BinanceTimeInForce timeInForce = BinanceTimeInForce.GOOD_TILL_CANCELLED;
    Double quantity;
    Double quoteOrderQty;
    Double price;
    String newClientOrderId = "";
    Double stopPrice;
    Double icebergQty;
    Long recvWindow;


    public String getAsQuery() throws BinanceApiException {
        StringBuffer sb = new StringBuffer();
        Escaper esc = UrlEscapers.urlFormParameterEscaper();
        if (symbol == null) {
            throw new BinanceApiException("Order Symbol is not set");
        }
        if (type == null) {
            throw new BinanceApiException("Order type is not set");
        }
        if (side == null) {
            throw new BinanceApiException("Order side is not set");
        }
        switch(type){
            case MARKET:
                if(quantity==null && quoteOrderQty == null){
                    throw new BinanceApiException("MARKET order requires either quantity or quoteOrderQty to be set.");
                }
                if(quantity!=null){
                    if (quantity.compareTo(0d) <= 0) {
                        throw new BinanceApiException("MARKET order requires a quantity >= 0");
                    }
                }else{
                    if (quoteOrderQty.compareTo(0d) <= 0) {
                        throw new BinanceApiException("MARKET order requires a quoteOrderQty >= 0");
                    }
                }
                break;
            case LIMIT:
                if(timeInForce==null || quantity==null || price == null){
                    throw new BinanceApiException("LIMIT order requires timeInForce, quantity and price to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new BinanceApiException("LIMIT order requires a quantity >= 0");
                }
                break;
            case STOP_LOSS:
                if(stopPrice==null || quantity==null){
                    throw new BinanceApiException("STOP_LOSS order requires quantity and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new BinanceApiException("STOP_LOSS order requires a quantity >= 0");
                }
                break;
            case TAKE_PROFIT:
                if(quantity==null || stopPrice == null){
                    throw new BinanceApiException("TAKE_PROFIT order requires quantity and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new BinanceApiException("TAKE_PROFIT order requires a quantity >= 0");
                }
                break;
            case STOP_LOSS_LIMIT:
                if(timeInForce==null || quantity==null || price == null || stopPrice == null){
                    throw new BinanceApiException("STOP_LOSS_LIMIT order requires timeInForce, quantity, price and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new BinanceApiException("STOP_LOSS_LIMIT order requires a quantity >= 0");
                }
                break;
            case TAKE_PROFIT_LIMIT:
                if(timeInForce==null || quantity==null || price == null || stopPrice == null){
                    throw new BinanceApiException("TAKE_PROFIT_LIMIT order requires timeInForce, quantity, price and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new BinanceApiException("TAKE_PROFIT_LIMIT order requires a quantity >= 0");
                }
                break;
            case LIMIT_MAKER:
                if(quantity==null || price == null){
                    throw new BinanceApiException("LIMIT_MAKER order requires quantity and price to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new BinanceApiException("LIMIT_MAKER order requires a quantity >= 0");
                }
                break;
        }
        // timestamp is also required, but will added implicitly later...
        sb.append("&symbol=").append(symbol);
        sb.append("&side=").append(side.name());
        sb.append("&type=").append(type.name());
        if (timeInForce != null) {
            sb.append("&timeInForce=").append(timeInForce.name());
        }
        if (quantity != null) {
            sb.append("&quantity=").append(quantity);
        }
        if (quoteOrderQty != null) {
            sb.append("&quoteOrderQty=").append(quoteOrderQty);
        }
        if(price !=null){
            sb.append("&price=").append(price);
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
        if(recvWindow !=null){
            sb.append("&recvWindow=").append(recvWindow);
        }
        return sb.toString().substring(1); // skipping the first &
    }
}
