package com.webcerebrium.binance.datatype;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.lang.reflect.Constructor;

@Data
@RequiredArgsConstructor
public class BinanceWithdrawOrder {
    @NonNull
    private String coin; // required
    @NonNull
    private String address; // required
    @NonNull
    private Double amount; // required
    private Long timestamp = System.currentTimeMillis(); // required
    /** client id for withdraw. */
    private String withdrawOrderId;
    private String network;
    /** Secondary address identifier for coins like XRP,XMR etc.. */
    private String addressTag;
    /** NO 	When making internal transfer, true for returning the fee to the destination
     * account; false for returning the fee back to the departure account. Default false. */
    private boolean transactionFeeFlag;
    /** Description of the address. Space in name should be encoded into %20. */
    private String name;
    private Long recvWindow;

    public String toQueryString() {
        String result = "";
        result += "?coin="+coin;
        result += "&address="+address;
        result += "&amount="+amount;
        if(timestamp!=null){
            result += "&timestamp="+timestamp;
        }
        if(withdrawOrderId!=null){
            result += "&withdrawOrderId="+withdrawOrderId;
        }
        if(network!=null){
            result += "&network="+network;
        }
        if(addressTag!=null){
            result += "&addressTag="+addressTag;
        }
        result += "&transactionFeeFlag="+transactionFeeFlag;
        if(recvWindow!=null){
            result += "&recvWindow="+recvWindow;
        }
        if(name!=null){
            result += "&name="+name;
        }
        return result;
    }
}
