package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class BinanceDeletedOrder {

    String symbol;
    String origClientOrderId;
    Long orderId;
    Long orderListId;
    String clientOrderId;
    Double price;
    Double origQty;
    Double executedQty;
    Double cummulativeQuoteQty;
    BinanceOrderStatus status;
    BinanceTimeInForce timeInForce;
    BinanceOrderType type;
    BinanceOrderSide side;

    public BinanceDeletedOrder(JsonObject ob) {
        symbol = ob.get("symbol").getAsString();
        origClientOrderId = ob.get("origClientOrderId").getAsString();
        clientOrderId = ob.get("clientOrderId").getAsString();
        status = BinanceOrderStatus.valueOf(ob.get("status").getAsString());
        timeInForce = BinanceTimeInForce.valueOf(ob.get("timeInForce").getAsString());
        type = BinanceOrderType.valueOf(ob.get("type").getAsString());
        side = BinanceOrderSide.valueOf(ob.get("side").getAsString());
    }
}
