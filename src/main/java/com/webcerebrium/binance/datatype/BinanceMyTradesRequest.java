package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BinanceMyTradesRequest {
    /** The symbol, required. */
    @NonNull
    BinanceSymbol symbol;
    Integer orderId;
    /** Start of query period, maximal 1 h duration. */
    Long startTime;
    /** End of query period, maximal 1 h duration. */
    Long endTime;
    /** Trade id to fetch from. Default gets most recent trades. */
    Long fromId;
    /** Max number of results, Default 500; max 1000. */
    Integer limit = 500;
    Long recvWindow = 5000L;
    long timestamp = System.currentTimeMillis();

    public String toQueryString(){
        String result = "?symbol="+symbol;
        result += "&timestamp="+timestamp;
        if(orderId!=null){
            result += "&orderId="+orderId;
        }
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
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        return result;
    }
}
