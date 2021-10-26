package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinanceDeleteOrderRequest {
    /** The symbol. */
    BinanceSymbol symbol;
    Integer recvWindow = 5000;
    long timestamp = System.currentTimeMillis();

    public String toQueryString(){
        String result = "?symbol="+symbol;
        result += "&timestamp="+timestamp;
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        return result;
    }
}
