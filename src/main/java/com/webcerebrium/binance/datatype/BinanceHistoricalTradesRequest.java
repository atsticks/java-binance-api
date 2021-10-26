package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BinanceHistoricalTradesRequest {
    /** The symbol, required. */
    @NonNull
    BinanceSymbol symbol;
    /** Trade id to fetch from. Default gets most recent trades. */
    Long fromId;
    /** Max number of results, Default 500; max 1000. */
    Integer limit = 500;

    public String toQueryString(){
        String result = "?symbol="+symbol;
        if(limit!=null){
            result += "&limit="+limit;
        }
        if(fromId!=null){
            result += "&fromId="+fromId;
        }
        return result;
    }
}
