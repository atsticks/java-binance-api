package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BinanceAggregatedTradesRequest {
    /** The symbol, required. */
    @NonNull
    BinanceSymbol symbol;
    /** Trade id to fetch from. Default gets most recent trades. */
    Long fromId;
    /** Start of query period, maximal 1 h duration. */
    Long startTime;
    /** End of query period, maximal 1 h duration. */
    Long endTime;
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
        if(startTime!=null){
            result += "&startTime="+startTime;
        }
        if(endTime!=null){
            result += "&endTime="+endTime;
        }
        return result;
    }
}
