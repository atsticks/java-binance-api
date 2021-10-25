package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceNewOrder {
    String symbol;
    Long orderId;
    Long orderListId;
    String clientOrderId;
    Long transactTime;
    boolean test;
}
