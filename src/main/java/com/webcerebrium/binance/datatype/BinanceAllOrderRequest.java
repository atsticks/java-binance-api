package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BinanceAllOrderRequest {
    @NonNull
    BinanceSymbol symbol;
    Long orderId;
    String origClientOrderId;
    Long recvWindow = 5000L;
    Long startTime;
    Long endTime;
    int limit = 500;
    long timestamp = System.currentTimeMillis();

    public String toQueryString(){
        String result = "?symbol="+symbol;
        result += "&timestamp="+timestamp;
        if(orderId!=null){
            result += "&orderId="+orderId;
        }
        if(origClientOrderId!=null){
            result += "&origClientOrderId="+origClientOrderId;
        }
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        if(startTime!=null){
            result += "&startTime="+startTime;
        }
        if(endTime!=null){
            result += "&endTime="+endTime;
        }
        result += "&limit="+limit;
        return result;
    }

}
