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
import com.webcerebrium.binance.api.ApiException;
import lombok.*;

import java.text.NumberFormat;
import java.util.Locale;


@Data
@RequiredArgsConstructor
public class OrderPlacement implements HasSymbol{
    @NonNull
    String symbol;
    @NonNull
    OrderSide side;
    OrderType type = OrderType.LIMIT;
    TimeInForce timeInForce = TimeInForce.GTC;
    Double quantity;
    Double quoteOrderQty;
    Double price;
    String newClientOrderId = "";
    Double stopPrice;
    Double icebergQty;
    Long trailingDelta;
    Long recvWindow;

    private ThreadLocal<NumberFormat> qtyFormat = ThreadLocal.withInitial(() -> {
        NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
        nf.setMaximumFractionDigits(3);
        return nf;
    });

    public String getAsQuery() throws ApiException {
        StringBuffer sb = new StringBuffer();
        Escaper esc = UrlEscapers.urlFormParameterEscaper();
        if (symbol == null) {
            throw new ApiException("Order Symbol is not set");
        }
        if (type == null) {
            throw new ApiException("Order type is not set");
        }
        if (side == null) {
            throw new ApiException("Order side is not set");
        }
        switch(type){
            case MARKET:
                if(quantity==null && quoteOrderQty == null){
                    throw new ApiException("MARKET order requires either quantity or quoteOrderQty to be set.");
                }
                if(quantity!=null){
                    if (quantity.compareTo(0d) <= 0) {
                        throw new ApiException("MARKET order requires a quantity >= 0");
                    }
                }else{
                    if (quoteOrderQty.compareTo(0d) <= 0) {
                        throw new ApiException("MARKET order requires a quoteOrderQty >= 0");
                    }
                }
                break;
            case LIMIT:
                if(timeInForce==null || quantity==null || price == null){
                    throw new ApiException("LIMIT order requires timeInForce, quantity and price to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new ApiException("LIMIT order requires a quantity >= 0");
                }
                break;
            case STOP_LOSS:
                if(stopPrice==null || quantity==null){
                    throw new ApiException("STOP_LOSS order requires quantity and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new ApiException("STOP_LOSS order requires a quantity >= 0");
                }
                break;
            case TAKE_PROFIT:
                if(quantity==null || stopPrice == null){
                    throw new ApiException("TAKE_PROFIT order requires quantity and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new ApiException("TAKE_PROFIT order requires a quantity >= 0");
                }
                break;
            case STOP_LOSS_LIMIT:
                if(timeInForce==null || quantity==null || price == null || stopPrice == null){
                    throw new ApiException("STOP_LOSS_LIMIT order requires timeInForce, quantity, price and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new ApiException("STOP_LOSS_LIMIT order requires a quantity >= 0");
                }
                break;
            case TAKE_PROFIT_LIMIT:
                if(timeInForce==null || quantity==null || price == null || stopPrice == null){
                    throw new ApiException("TAKE_PROFIT_LIMIT order requires timeInForce, quantity, price and stopPrice to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new ApiException("TAKE_PROFIT_LIMIT order requires a quantity >= 0");
                }
                break;
            case LIMIT_MAKER:
                if(quantity==null || price == null){
                    throw new ApiException("LIMIT_MAKER order requires quantity and price to be set.");
                }
                if (quantity.compareTo(0d) <= 0) {
                    throw new ApiException("LIMIT_MAKER order requires a quantity >= 0");
                }
                break;
        }
        // timestamp is also required, but will added implicitly later...
        sb.append("&symbol=").append(symbol);
        sb.append("&side=").append(side.name());
        sb.append("&type=").append(type.name());
        if (timeInForce != null) {
            switch(type){
                case LIMIT:
                case STOP_LOSS_LIMIT:
                case TAKE_PROFIT_LIMIT:
                    sb.append("&timeInForce=").append(timeInForce.name());
                    break;
            }

        }
        if (quantity != null) {
            sb.append("&quantity=").append(formatQuantity(quantity));
        }
        if (quoteOrderQty != null) {
            sb.append("&quoteOrderQty=").append(formatQuantity(quoteOrderQty));
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
            sb.append("&icebergQty=").append(formatQuantity(icebergQty));
        }
        if (trailingDelta != null) {
            sb.append("&trailingDelta=").append(formatQuantity(trailingDelta));
        }
        if(recvWindow !=null){
            sb.append("&recvWindow=").append(recvWindow);
        }
        return sb.toString().substring(1); // skipping the first &
    }

    private String formatQuantity(double qty) {
        return this.qtyFormat.get().format(qty);
    }
}
