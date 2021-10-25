package com.webcerebrium.binance.datatype;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

/*
"symbol": "LTCBTC",
"bidPrice": "4.00000000",
"bidQty": "431.00000000",
"askPrice": "4.00000200",
"askQty": "9.00000000"
*/

import com.google.gson.JsonObject;
import lombok.Data;



@Data
public class BinanceTicker {
    public String symbol = null;
    public Double bidPrice = null;
    public Double bidQty = null;
    public Double askPrice = null;
    public Double askQty = null;

    public void read(JsonObject ob){
        askPrice = ob.get("askPrice").getAsDouble();
        askQty = ob.get("askQty").getAsDouble();
        bidPrice = ob.get("bidPrice").getAsDouble();
        bidQty = ob.get("bidQty").getAsDouble();
    }

}
