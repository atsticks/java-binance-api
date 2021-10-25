package com.webcerebrium.binance.datatype;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinanceClosedOrderRequest {
    /** start time in Milliseconds. */
    private Long start;
    /** end time in Milliseconds */
    private Long end;
    /** limit, default 500; max 1000.*/
    private Integer limit = 500;
    /** offset, start with 0; default 0.*/
    private Integer offset = 0;
    /** order side. 1 for buy and 2 for sell. */
    private BinanceOrderSide side;
    /** order status list. Allowed value: [Ack, IocExpire, IocNoFill, FullyFill, Canceled, Expired, FailedBlocking, FailedMatching]. */
    private BinanceOrderStatus2 status;
    /** The symbol. */
    private BinanceSymbol symbol;
    /** total number required, 0 for not required and 1 for required; default not required, return total=-1 in response. */
    private boolean total;

    public String toQueryString(){
        String result = "";
        if(start!=null){
            result += "&start="+start;
        }
        if(end!=null){
            result += "&end="+end;
        }
        if(limit!=null){
            result += "&limit="+limit;
        }
        if(offset!=null){
            result += "&offset="+offset;
        }
        if(side!=null){
            result += "&side="+side.getNumeric();
        }
        if(status!=null){
            result += "&status="+status.toString();
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
