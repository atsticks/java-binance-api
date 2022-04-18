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

package com.webcerebrium.binance.datatype.events;
import com.google.gson.JsonObject;
import com.webcerebrium.binance.api.ApiException;
import com.webcerebrium.binance.datatype.*;
import lombok.Data;
import lombok.EqualsAndHashCode;



/*
 {
 "e": "executionReport",		// order or trade report
 "E": 1499406026404,			// event time
 "s": "ETHBTC",					// symbol
 "c": "1hRLKJhTRsXy2ilYdSzhkk",	// newClientOrderId
 "S": "BUY",					// side: buy or sell
 "o": "LIMIT",					// order type LIMIT, MARKET
 "f": "GTC",					// time in force, GTC: Good Till Cancel, IOC: Immediate or Cancel
 "q": "22.42906458",			// original quantity
 "p": "0.10279999",				// price
 "P": "0.00000000", //? undocumented?
 "F": "0.00000000", //? undocumented?
 "g": -1,           //? undocumented?
 "C": "null",       //? undocumented?
 "x": "TRADE",					// executionType NEW, CANCELED, REPLACED, REJECTED, TRADE,EXPIRED
 "X": "NEW", 				    // orderStatus NEW, PARTIALLY_FILLED, FILLED, CANCELED，PENDING_CANCEL, REJECTED, EXPIRED
 "r": "NONE", 					// orderRejectReason，NONE, UNKNOWN_INSTRUMENT, MARKET_CLOSED, PRICE_QTY_EXCEED_HARD_LIMITS, UNKNOWN_ORDER, DUPLICATE_ORDER, UNKNOWN_ACCOUNT, INSUFFICIENT_BALANCE, ACCOUNT_INACTIVE, ACCOUNT_CANNOT_SETTLE
 "i": 4294220,					// orderid

 "l": "17.42906458",				// quantity of last filled trade
 "z": "22.42906458",				// accumulated quantity of filled trades on this order
 "L": "0.10279999",				// price of last filled trade
 "n": "0.00000001",				// commission
 "N": "BNC",						// asset on which commission is taken
 "T": 1499406026402,				// trade time
 "t": 77517,						// trade id
 "I": 8644124,					// can be ignored
 "w": false,						// can be ignored
 "m": false,						// is buyer maker
 "M": true						// can be ignored
 }
*/

@Data
@EqualsAndHashCode(of = {"symbol", "eventTime", "orderId", "tradeId"})
public class ExecutionReportEvent implements HasSymbol {

    public Long eventTime;
    public String symbol;
    public String newClientOrderId;
    public OrderSide side;
    public OrderType type;
    public TimeInForce timeInForce;

    public Double quantity;
    public Double price;

    public ExecutionType executionType;
    public OrderStatus status;
    public RejectReason rejectReason;

    public Long orderId;
    public Double quantityOfLastFilledTrade;
    public Double accumulatedQuantityOfFilledTrades;
    public Double priceOfLastFilledTrade;
    public Double commission;

    public String assetOfCommission;
    public Long tradeTime;
    public Long tradeId;
    public boolean isMaker;

    public ExecutionReportEvent(JsonObject event) throws ApiException {
        eventTime = event.get("E").getAsLong();
        symbol = event.get("s").getAsString();
        newClientOrderId = event.get("c").getAsString();

        side = OrderSide.valueOf(event.get("S").getAsString()); // was using "c" again
        type = OrderType.valueOf(event.get("o").getAsString());
        timeInForce = TimeInForce.valueOf(event.get("f").getAsString());

        price = event.get("p").getAsDouble();
        quantity = event.get("q").getAsDouble();

        executionType = ExecutionType.valueOf(event.get("x").getAsString());
        status = OrderStatus.valueOf(event.get("X").getAsString());
        rejectReason = RejectReason.valueOf(event.get("r").getAsString());

        orderId = event.get("i").getAsLong();

        quantityOfLastFilledTrade = event.get("l").getAsDouble();
        accumulatedQuantityOfFilledTrades = event.get("z").getAsDouble();
        priceOfLastFilledTrade = event.get("L").getAsDouble();
        commission = event.get("n").getAsDouble();

        //assetOfCommission = event.get("N").getAsString();
        assetOfCommission = (event.get("N").isJsonNull()? "" : event.get("N").getAsString()); // Binance API returns null for orders, only used for trades

        tradeTime = event.get("T").getAsLong();
        tradeId = event.get("t").getAsLong();
        isMaker = event.get("m").getAsBoolean();
    }
}
