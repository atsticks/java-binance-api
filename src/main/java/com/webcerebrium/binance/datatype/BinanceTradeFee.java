package com.webcerebrium.binance.datatype;

import lombok.Data;

@Data
public class BinanceTradeFee {

    String symbol;
    Double makerCommission;
    Double takerCommission;
    long timestamp = System.currentTimeMillis();
}
