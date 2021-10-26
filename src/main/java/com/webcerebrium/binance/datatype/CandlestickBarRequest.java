package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
public class CandlestickBarRequest {

    @NonNull
    BinanceSymbol symbol;
    @NonNull
    BinanceInterval interval;
    /** Start of query period, maximal 1 h duration. */
    Long startTime;
    /** End of query period, maximal 1 h duration. */
    Long endTime;
    int limit = 500;

    public String toQueryString(){
        String result = "?symbol="+symbol;
        result+= "&interval="+interval;
        result += "&limit="+limit;
        if(startTime!=null){
            result += "&startTime="+startTime;
        }
        if(endTime!=null){
            result += "&endTime="+endTime;
        }
        return result;
    }
}
