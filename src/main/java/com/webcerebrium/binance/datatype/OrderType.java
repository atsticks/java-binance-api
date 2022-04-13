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

/**
 * Any LIMIT or LIMIT_MAKER type order can be made an iceberg order by sending an icebergQty.
 *
 * Any order with an icebergQty MUST have timeInForce set to GTC.
 *
 * MARKET orders using the quantity field specifies the amount of the base asset the user wants to buy or sell at the market price.
 * For example, sending a MARKET order on BTCUSDT will specify how much BTC the user is buying or selling.
 */
public enum OrderType {
    /** Mandatroy fields: timeInForce, quantity, price. */
    LIMIT,
    /**
     * Mandatory fields: symbol, side, type, timestamp, quantity or quoteOrderQty.
     *
     * MARKET orders using quoteOrderQty specifies the amount the user wants to spend (when buying) or receive (when selling) the quote asset; the correct quantity will be determined based on the market liquidity and quoteOrderQty.
     * Using BTCUSDT as an example:
     * On the BUY side, the order will buy as many BTC as quoteOrderQty USDT can.
     * On the SELL side, the order will sell as much BTC needed to receive quoteOrderQty USDT.
     *
     * MARKET orders using quoteOrderQty will not break LOT_SIZE filter rules; the order will execute a quantity that will have the notional value as close as possible to quoteOrderQty.
     */
    MARKET,
    /**
     * Mandatory fields: symbol, side, type, timestamp, quantity, price.
     *
     * LIMIT_MAKER are LIMIT orders that will be rejected if they would immediately match and trade as a taker.
     */
    LIMIT_MAKER,
    /**
     * Mandatory fields: symbol, side, type, timestamp, quantity, stopPrice.
     *
     * STOP_LOSS and TAKE_PROFIT will execute a MARKET order when the stopPrice is reached.
     */
    STOP_LOSS,
    /** Mandatory fields: symbol, side, type, timestamp, timeInForce, quantity, price, stopPrice. */
    STOP_LOSS_LIMIT,
    /** Mandatory fields: symbol, side, type, timestamp, quantity, stopPrice.
     *
     * STOP_LOSS and TAKE_PROFIT will execute a MARKET order when the stopPrice is reached.
     */
    TAKE_PROFIT,
    /** Mandatory fields: symbol, side, type, timestamp, timeInForce, quantity, price, stopPrice. */
    TAKE_PROFIT_LIMIT;

}
