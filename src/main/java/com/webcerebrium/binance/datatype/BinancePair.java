package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class BinancePair {

   // Required: base_asset_symbol,list_price,lot_size,quote_asset_symbol,tick_size
    String symbol; //
    String baseSymbol; // base_asset_symbol:
    String quoteSymbol;
    boolean sellAllowed;
    boolean buyAllowed;
    boolean marginTrade;

    public BinancePair(JsonObject ob) {
        symbol = ob.get("symbol").getAsString();
        baseSymbol = ob.get("base").getAsString();
        quoteSymbol = ob.get("quote").getAsString();
        sellAllowed = ob.get("isSellAllowed").getAsBoolean();
        buyAllowed = ob.get("isBuyAllowed").getAsBoolean();
        marginTrade = ob.get("isMarginTrade").getAsBoolean();
    }
}
