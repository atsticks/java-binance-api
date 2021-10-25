package com.webcerebrium.binance.datatype;
/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

import com.google.gson.JsonArray;
import lombok.Data;



@Data
public class BinanceBidOrAsk {

    public BinanceBidType type;
    public Double price = null;
    public Double quantity = null;

    public BinanceBidOrAsk() {}

    public BinanceBidOrAsk(BinanceBidType type, JsonArray arr) {
        this.type = type;
        this.price = arr.get(0).getAsDouble();
        this.quantity = arr.get(1).getAsDouble();
    }
}
