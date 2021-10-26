package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;


@Data
@Builder
public class BinanceFiatOrderRequest {
    /** the owner address. */
    @NonNull
    BinanceFiatTransactionType transactionType;
    Long beginTime;
    Long endTime;
    int page=1;
    int rows = 100;
    /** limit, default 500; max 1000.*/
    @NonNull
    long timestamp = System.currentTimeMillis();
    /** offset, start with 0; default 0.*/
    Long recvWindow = 0L;

    public String toQueryString(){
        String result = "?";
        if(transactionType==BinanceFiatTransactionType.deposit)
            result += "0";
        else
            result += "1";
        if(beginTime!=null){
            result += "&beginTime="+beginTime;
        }
        if(endTime!=null){
            result += "&endTime="+endTime;
        }
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        result += "&timestamp="+timestamp;
        result += "&page="+page;
        result += "&rows="+rows;
        return result;
    }
}
