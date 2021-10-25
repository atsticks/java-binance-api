package com.webcerebrium.binance.datatype;

/* ============================================================
 * java-binance-api
 * https://github.com/webcerebrium/java-binance-api
 * ============================================================
 * Copyright 2017-, Viktor Lopata, Web Cerebrium OÜ
 * Released under the MIT License
 * ============================================================ */

public enum BinanceOrderSide {
    BUY,
    SELL;

    int getNumeric(){
        switch(this){
            case BUY:
                return 1;
            case SELL:
            default:
                return 2;
        }
    }
}
