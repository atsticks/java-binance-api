package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/*
    address*
String

Required
limit
Integer (int32)
default 500; max 1000.
offset
Integer (int32)
start with 0; default 0.
symbol
String
symbol
total
Integer (int32)
total number required, 0 for not required and 1 for required; default not required, return total=-1 in response

     */
@Data
@RequiredArgsConstructor
public class BinanceOpenOrderRequest {
    /** the owner address. */
    private String address;
    /** limit, default 500; max 1000.*/
    private Integer limit = 500;
    /** offset, start with 0; default 0.*/
    private Integer offset = 0;
    /** The symbol. */
    private BinanceSymbol symbol;
    /** total number required, 0 for not required and 1 for required; default not required, return total=-1 in response. */
    private boolean total;

    public String toQueryString(){
        String result = "";
        if(limit!=null){
            result += "&limit="+limit;
        }
        if(offset!=null){
            result += "&offset="+offset;
        }
        if(symbol!=null){
            result += "&symbol="+symbol.toString();
        }
        if(total){
            result += "&total=1";
        }
        if(result.length()>0){
            return result.replaceFirst("\\&", "?");
        }
        return result;
    }
}
