package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceNewOrder {
    public String symbol;
    public Long orderId;
    public Long orderListId;
    public String clientOrderId;
    public Long transactTime;
    public boolean test;
}
