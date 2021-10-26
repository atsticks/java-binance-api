package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class BinanceOrderRequest {
    @NonNull
    BinanceSymbol symbol;
    Long orderId;
    String origClientOrderId;
    Long recvWindow;
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
        return result;
    }

}
