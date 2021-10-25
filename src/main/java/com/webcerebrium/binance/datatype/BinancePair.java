package com.webcerebrium.binance.datatype;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class BinancePair {

   // Required: base_asset_symbol,list_price,lot_size,quote_asset_symbol,tick_size
    String baseSymbol; // base_asset_symbol:
    String quoteSymbol;
    Double listPrice;
    /** Minimium price change in decimal form, example: 0.00000001. */
    Double tickSize;
    /** Minimium trading quantity in decimal form, example: 1.00000000. */
    Double lotSize;

    public BinancePair(JsonObject ob) {
        baseSymbol = ob.get("base_asset_symbol").getAsString();
        quoteSymbol = ob.get("quote_asset_symbol").getAsString();
        listPrice = ob.get("list_price").getAsDouble();
        tickSize = ob.get("tick_size").getAsDouble();
        lotSize = ob.get("lot_size").getAsDouble();
    }
}
